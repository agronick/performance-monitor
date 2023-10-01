package com.aatorque.stats

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import timber.log.Timber
import org.prowl.torque.remote.ITorqueService
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

typealias PidInfo = List<Pair<String, List<String>>>
class TorqueServiceWrapper: Service() {
    private val binder = LocalBinder()
    var wasStartAttempted = false
    var torqueService: ITorqueService? = null
    val onConnect = ArrayList<(ITorqueService) -> Unit>()
    var pids: PidInfo? = null
    var connectCount = 0
    val conLock = ReentrantLock()

    override fun onCreate() {
        super.onCreate()
        if (!wasStartAttempted) {
            wasStartAttempted = startTorque()
        }
        connectCount++
    }

    override fun onDestroy() {
        super.onDestroy()
        connectCount--
        if (connectCount == 0) {
            unbindService(torqueConnection)
        }
    }

    class ListPids(val ts: ITorqueService, val onComplete: (PidInfo) -> Unit): Runnable {
        override fun run() {
            val pids = ts.listAllPIDs()
            val pidAttributes = ts.getPIDInformation(pids).map { it.split(",") }

            onComplete(
                pids.zip(pidAttributes).sortedBy { it.second[0] }
            )
        }

    }

    fun loadPidInformation(force: Boolean = false, onComplete: ((PidInfo) -> Unit)? = null) {
        if (pids != null && !force) {
            onComplete?.invoke(pids!!)
            return
        }
        addConnectCallback {
            val bg = ListPids(it) {
                newPids ->
                this.pids = newPids
                onComplete?.invoke(newPids)
            }
            Thread(bg).start()
        }
    }
    /**
     * Class used for the client Binder. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): TorqueServiceWrapper = this@TorqueServiceWrapper
    }

    fun isAvailable(): Boolean {
        return torqueService != null
    }

    fun addConnectCallback(func: (ITorqueService) -> Unit): TorqueServiceWrapper {
        if (torqueService == null) {
            conLock.withLock {
                onConnect.add(func)
            }
        } else {
            func(torqueService!!)
        }
        return this
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }


    private val torqueConnection: ServiceConnection = object : ServiceConnection {
        /**
         * What to do when we get connected to Torque.
         *
         * @param arg0
         * @param service
         */
        override fun onServiceConnected(arg0: ComponentName, service: IBinder) {
            val svc = ITorqueService.Stub.asInterface(service)
            conLock.withLock {
                for (funt in onConnect) {
                    funt(svc)
                }
                onConnect.clear()
            }
            torqueService = svc
        }

        /**
         * What to do when we get disconnected from Torque.
         *
         * @param name
         */
        override fun onServiceDisconnected(name: ComponentName) {
            torqueService = null
        }
    }

    fun startTorque(): Boolean {
        val intent = Intent()
        intent.setClassName("org.prowl.torque", "org.prowl.torque.remote.TorqueService")
        val torqueBind = bindService(intent, torqueConnection, Activity.BIND_AUTO_CREATE)
        Timber.i(
            if (torqueBind) "Connected to torque service!" else "Unable to connect to Torque plugin service"
        )
        return torqueBind
    }

    companion object {
        fun runStartIntent(context: Context, conn: ServiceConnection): Boolean {
            Intent(context, TorqueServiceWrapper::class.java).also { intent ->
                return context.bindService(intent, conn, Context.BIND_AUTO_CREATE)
            }
        }
    }
}
