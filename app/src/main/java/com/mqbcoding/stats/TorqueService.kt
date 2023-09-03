package com.mqbcoding.stats

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import org.prowl.torque.remote.ITorqueService

class TorqueService {
    val TAG = "TorqueService"
    var torqueService: ITorqueService? = null
    val onConnect = ArrayList<(ITorqueService) -> Unit>()

    fun isAvailable(): Boolean {
        return torqueService != null
    }

    fun addConnectCallback(func: (ITorqueService) -> Unit): TorqueService {
        if (torqueService == null) {
            onConnect.add(func)
        } else {
            func(torqueService!!)
        }
        return this
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

    fun onDestroy(context: Context) {
        if (torqueService != null) {
            context.unbindService(torqueConnection)
        }
    }

    fun requestQuit(context: Context) {
        context.sendBroadcast(Intent("org.prowl.torque.REQUEST_TORQUE_QUIT"))
        Log.d(TAG, "Torque stop")
    }
    fun startTorque(context: Context): Boolean {
        val intent = Intent()
        intent.setClassName("org.prowl.torque", "org.prowl.torque.remote.TorqueService")
        val torqueBind = context.bindService(intent, torqueConnection, Activity.BIND_AUTO_CREATE)
        Log.d(
            TAG,
            if (torqueBind) "Connected to torque service!" else "Unable to connect to Torque plugin service"
        )
        return torqueBind
    }

}