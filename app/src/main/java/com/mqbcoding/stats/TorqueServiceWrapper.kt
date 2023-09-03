package com.mqbcoding.stats

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.util.Log
import org.prowl.torque.remote.ITorqueService
import kotlinx.coroutines.*

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

    override fun onCreate() {
        super.onCreate()
        wasStartAttempted = startTorque()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(torqueConnection)
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
            onConnect.add(func)
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
            for (funt in onConnect) {
                funt(service)
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

    companion object {
        fun getConnection(forceLoad: Boolean? = false): ServiceConnection {
            return object : ServiceConnection {
                var mBound = false
                lateinit var service: TorqueServiceWrapper
                override fun onServiceConnected(className: ComponentName, service: IBinder) {
                    if (forceLoad != null) {
                        this.service =
                            (service as TorqueServiceWrapper.LocalBinder).getService()
                        this.service.loadPidInformation(forceLoad)
                    }
                    mBound = true
                }

                override fun onServiceDisconnected(arg0: ComponentName) {
                    mBound = false
                }
            }
        }
    }
}