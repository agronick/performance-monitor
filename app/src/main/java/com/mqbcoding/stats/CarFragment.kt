package com.mqbcoding.stats

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.apps.auto.sdk.StatusBarController
import org.prowl.torque.remote.ITorqueService

abstract class CarFragment : Fragment() {
    private val TAG = "CarFragment"

    var title: String? = null
    fun setTitle(@StringRes resId: Int) {
        title = context!!.getString(resId)
    }

    abstract fun setupStatusBar(sc: StatusBarController)
}