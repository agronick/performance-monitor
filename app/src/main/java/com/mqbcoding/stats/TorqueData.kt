package com.mqbcoding.stats

import java.math.BigInteger

class TorqueData() {
    var lastData: Double? = null
    var pid: String? = null
    var pidInt: Long? = null
    var query = "none"

    var longName = ""
    var shortName = ""
    var minValue = 0
    var maxValue = 1
    var unit = ""
    var scale = 0f

    var notifyUpdate: ((Double) -> Unit)? = null
    companion object {
        const val PREFIX = "torque_"
    }

    constructor(value: String): this() {
        if (value.startsWith(PREFIX)) {
            pid = value.substring(PREFIX.length)
            val splitParts = value.split("_")
            pidInt = BigInteger(splitParts[splitParts.size - 1].split(",")[0], 16).toLong()
            query = value
        }
    }

    fun setLastData(value: Double) {
        lastData = value
        if (notifyUpdate != null) {
            notifyUpdate?.invoke(value)
        }
    }

}