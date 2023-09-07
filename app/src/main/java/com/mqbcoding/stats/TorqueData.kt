package com.mqbcoding.stats

import android.util.Log
import androidx.core.text.isDigitsOnly
import com.ezylang.evalex.BaseException
import com.ezylang.evalex.Expression
import com.mqbcoding.datastore.Display
import java.math.BigInteger
import java.text.ParseException
import java.util.concurrent.ScheduledFuture

class TorqueData(val display: Display) {

    var lastData: Double? = null
    var pid: String? = null
    var pidInt: Long? = null
    var minValue: Double = 0.0
    var maxValue: Double = 0.0
    var expression: Expression? = null
    var lastDataStr: String? = null
    var refreshTimer: ScheduledFuture<*>? = null
    var parseError = false

    var notifyUpdate: ((TorqueData) -> Unit)? = null
    companion object {
        const val TAG = "TorqueData"
        const val PREFIX = "torque_"
        val drawableRegex = Regex("res/drawable/(?<name>.+)\\.[a-z]+")
        val numberRegex = Regex("-?[0-9,\\.]+")
    }

    init {
        val value = display.pid
        if (value.startsWith(PREFIX)) {
            pid = value.substring(PREFIX.length)
            val splitParts = value.split("_")
            pidInt = BigInteger(splitParts[splitParts.size - 1].split(",")[0], 16).toLong()
        }
    }

    fun setLastData(value: Double) {
        lastDataStr = convertIfNeeded(value)
        if (lastDataStr != null) {
            lastData = try {
                lastDataStr!!.toDouble()
            } catch (e: NumberFormatException) {
                lastData
            }
        }
        if (value > maxValue) {
            maxValue = value
        }
        if (value < minValue) {
            minValue = value
        }
        notifyUpdate?.invoke(this)
    }

    private fun convertIfNeeded(value: Double): String? {
        if (!display.enableScript || display.customScript == "" || parseError) return null
        if (expression == null) {
            expression = Expression(display.customScript)
        }
        return try {
            expression!!.with("a", value).evaluate().stringValue
        } catch (e: BaseException) {
            parseError = true
            Log.e(TAG, "Unable to parse")
            e.printStackTrace()
            "Error"
        }
    }

    fun getDrawableName(): String? {
        val match = drawableRegex.matchEntire(display.icon)
        if (match != null) {
            return match.groups["name"]!!.value
        }
        return null
    }

}