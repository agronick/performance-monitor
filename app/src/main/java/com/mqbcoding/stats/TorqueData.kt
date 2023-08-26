package com.mqbcoding.stats

import android.widget.TextView
import java.lang.ref.WeakReference

class TorqueData() {
    var lastData: Double? = null
    var pid: String? = null
    var query = "none"

    var longName = ""
    var shortName = ""
    var minValue = 0
    var maxValue = 1
    var unit = ""
    var scale = 0f

    var notifyUpdate: WeakReference<(Double) -> Unit>? = null
    companion object {
        const val PREFIX = "torque_"
    }

    constructor(value: String): this() {
        if (value.startsWith(PREFIX)) {
            pid = value.substring(PREFIX.length)
            query = value
        }
    }
    fun setNotifyUpdate(func: (Double) -> Unit) {
        notifyUpdate = WeakReference(func)
    }

    fun setLastData(value: Double) {
        lastData = value
        if (notifyUpdate != null) {
            notifyUpdate!!.get()?.let { it(value) }
        }
    }

}