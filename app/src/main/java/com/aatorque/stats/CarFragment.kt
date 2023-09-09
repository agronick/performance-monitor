package com.aatorque.stats

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.apps.auto.sdk.StatusBarController

abstract class CarFragment : Fragment() {
    private val TAG = "CarFragment"

    var title: String? = null
    fun setTitle(@StringRes resId: Int) {
        title = context!!.getString(resId)
    }

    abstract fun setupStatusBar(sc: StatusBarController)
}