package com.mqbcoding.stats

import androidx.javascriptengine.JavaScriptIsolate
import androidx.javascriptengine.JavaScriptSandbox
import com.mqbcoding.datastore.Display
import java.math.BigInteger

class TorqueData(val display: Display) {

    var lastData: Double? = null
    var pid: String? = null
    var pidInt: Long? = null
    var minValue: Double = 0.0
    var maxValue: Double = 0.0

    var notifyUpdate: ((TorqueData) -> Unit)? = null
    companion object {
        const val PREFIX = "torque_"
        val drawableRegex = Regex("res/drawable/(?<name>.+)\\.[a-z]+")
    }

    init {
        val value = display.pid
        if (value.startsWith(PREFIX)) {
            pid = value.substring(PREFIX.length)
            val splitParts = value.split("_")
            pidInt = BigInteger(splitParts[splitParts.size - 1].split(",")[0], 16).toLong()
        }
    }

    fun setLastData(value: Double, sandbox: JavaScriptIsolate) {
        val lastDataStr = convertIfNeeded(value, sandbox)
        lastData = value
        if (value > maxValue) {
            maxValue = value
        }
        if (value < minValue) {
            minValue = value
        }
        notifyUpdate?.invoke(this)
    }

    private fun convertIfNeeded(value: Double, sandbox: JavaScriptIsolate): String? {
        if (!display.enableJs || display.customJs == "") return null
        sandbox.evaluateJavaScriptAsync(
            display.customJs.replace("$$", value.toString(), false)
        )
    }

    fun getDrawableName(): String? {
        val match = drawableRegex.matchEntire(display.icon)
        if (match != null) {
            return match.groups["name"]!!.value
        }
        return null
    }

}