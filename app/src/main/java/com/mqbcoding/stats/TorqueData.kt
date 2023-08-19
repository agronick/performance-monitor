package com.mqbcoding.stats

import android.widget.TextView

abstract class TorqueData {
    var lastData: Double? = null
    var pidInfo: String? = null
    var pid: String? = null


    companion object {
        var powerUnits: Boolean? = null
        const val FORMAT_DECIMALS = "%.1f"
        const val FORMAT_DECIMALS_WITH_UNIT = "%.1f %s"
        const val FORMAT_DEGREES = "%.1f°"
        const val FORMAT_GFORCE = "%.1fG"
        const val FORMAT_KM = "%.1f km"
        const val FORMAT_MILES = "%.1f miles"
        const val FORMAT_NO_DECIMALS = "%.0f"
        const val FORMAT_PERCENT = "%.1f"
        const val FORMAT_DEGREESPEC = "%.1f°/s"
        const val FORMAT_TEMPERATURE = "%.1f°"
        const val FORMAT_VOLT = "%.1fV"
        const val FORMAT_TEMPERATURE0 = "-,-°"
        const val FORMAT_TEMPERATUREC = "%.1f°C"
        const val FORMAT_TEMPERATUREF = "%.1f°F"
        const val FORMAT_VOLT0 = "-,-V"
    }
    var query: String = "none"
        set(value) {
        lastData = null
        pidInfo = null
        pid = null
        if (value.startsWith("torque")) {
            pid = value.substring(value.lastIndexOf('_') + 1).substring(2)
        }
        field = value
    }
}