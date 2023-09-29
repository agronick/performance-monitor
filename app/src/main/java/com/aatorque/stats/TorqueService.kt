package com.aatorque.stats

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import org.prowl.torque.remote.ITorqueService
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class TorqueService {
    val TAG = "TorqueService"
    var torqueService: ITorqueService? = null
    val onConnect = ArrayList<(ITorqueService) -> Unit>()
    val conLock = ReentrantLock()
    var hasBound = false

    fun addConnectCallback(func: (ITorqueService) -> Unit): TorqueService {
        if (torqueService == null) {
            conLock.withLock {
                onConnect.add(func)
            }
        } else {
            func(torqueService!!)
        }
        return this
    }

    fun runIfConnected(func: (ITorqueService) -> Unit) {
        if (torqueService != null) {
            func(torqueService!!)
        }
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
            if (BuildConfig.SIMULATE_METRICS) {
                svc.setDebugTestMode(true)
            }
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

    fun onDestroy(context: Context) {
        if (hasBound) {
            context.unbindService(torqueConnection)
            hasBound = false
        }
    }

    fun requestQuit(context: Context) {
        context.sendBroadcast(Intent("org.prowl.torque.REQUEST_TORQUE_QUIT"))
        Log.d(TAG, "Torque stop")
    }
    fun startTorque(context: Context): Boolean {
        val intent = Intent()
        intent.setClassName("org.prowl.torque", "org.prowl.torque.remote.TorqueService")
        hasBound = context.bindService(intent, torqueConnection, Activity.BIND_AUTO_CREATE)
        Log.d(
            TAG,
            if (hasBound) "Connected to torque service!" else "Unable to connect to Torque plugin service"
        )
        return hasBound
    }

}