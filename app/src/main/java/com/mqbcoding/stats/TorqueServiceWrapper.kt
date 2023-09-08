package com.mqbcoding.stats

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.util.Log
import org.prowl.torque.remote.ITorqueService
import kotlinx.coroutines.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class TorqueServiceWrapper: Service() {
    // Binder given to clients.
    val TAG = "TorqueServiceWrapper"
    private val binder = LocalBinder()
    var wasStartAttempted = false
    var wasStartSuccessful = false
    var torqueService: ITorqueService? = null
    val onConnect = ArrayList<(ITorqueService) -> Unit>()
    var pids: Array<String>? = null
    var pidInfo: List<List<String>>? = null
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

    class ListPids(val ts: ITorqueService, val onComplete: (Array<String>, List<List<String>>) -> Unit): java.lang.Runnable {
        override fun run() {
            val pids = ts.listAllPIDs()
            val detailsQuery = ts.getPIDInformation(pids).map { it.split(",") }
            onComplete(pids, detailsQuery)
        }

    }

    fun loadPidInformation(force: Boolean = false, onComplete: ((Array<String>, List<List<String>>) -> Unit)? = null) {
        if (pids != null && pidInfo != null && !force) {
            onComplete?.invoke(pids!!, pidInfo!!)
            return
        }
        addConnectCallback {
            val bg = ListPids(it) {
                pids, details ->
                this.pids = pids
                this.pidInfo = details
                onComplete?.invoke(pids, details)
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
            val service = ITorqueService.Stub.asInterface(service)
            conLock.withLock {
                for (funt in onConnect) {
                    funt(service)
                }
                onConnect.clear()
            }
            torqueService = service
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
        Log.d(
            TAG,
            if (torqueBind) "Connected to torque service!" else "Unable to connect to Torque plugin service"
        )
        return torqueBind
    }

    fun requestQuit() {
        sendBroadcast(Intent("org.prowl.torque.REQUEST_TORQUE_QUIT"))
        Log.d(TAG, "Torque stop")
    }

    companion object {
        fun runStartIntent(context: Context, conn: ServiceConnection): Intent {
            return Intent(context, TorqueServiceWrapper::class.java).also { intent ->
                context.bindService(intent, conn, Context.BIND_AUTO_CREATE)
            }
        }
    }
}