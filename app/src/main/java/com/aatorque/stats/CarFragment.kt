package com.aatorque.stats

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.apps.auto.sdk.StatusBarController

abstract class CarFragment : Fragment() {

    var title: String? = null
    fun setTitle(@StringRes resId: Int) {
        title = requireContext().getString(resId)
    }

    abstract fun setupStatusBar(sc: StatusBarController)
}