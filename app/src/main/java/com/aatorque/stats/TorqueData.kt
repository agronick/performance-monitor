package com.aatorque.stats

import android.util.Log
import com.ezylang.evalex.BaseException
import com.ezylang.evalex.Expression
import com.ezylang.evalex.config.ExpressionConfiguration
import com.aatorque.datastore.Display
import java.math.BigInteger
import java.util.concurrent.ScheduledFuture

class TorqueData(val display: Display) {

    var pid: String? = null
    var pidInt: Long? = null
    var minValue: Double = 0.0
    var maxValue: Double = 0.0
    var expression: Expression? = null
    var lastDataStr: String? = null
    var refreshTimer: ScheduledFuture<*>? = null
    var parseError = false

    var notifyUpdate: ((TorqueData) -> Unit)? = null
        set(value) {
            field = value
            value?.let { it(this) }
        }

    var lastData: Double = 0.0
        set(value) {
            lastDataStr = convertIfNeeded(value)
            field = if (lastDataStr != null) {
                try {
                    lastDataStr!!.toDouble()
                } catch (e: NumberFormatException) {
                    lastData
                }
            } else {
                value
            }
            if (field > maxValue) {
                maxValue = field
            }
            if (field < minValue) {
                minValue = field
            }
        }
    companion object {
        const val TAG = "TorqueData"
        const val PREFIX = "torque_"
        val drawableRegex = Regex("res/drawable/(?<name>.+)\\.[a-z]+")
        val evalConfig: ExpressionConfiguration = ExpressionConfiguration.builder()
            .decimalPlacesRounding(2)
            .build()
    }

    init {
        val value = display.pid
        if (value.startsWith(PREFIX)) {
            pid = value.substring(PREFIX.length)
            val splitParts = value.split("_")
            pidInt = BigInteger(splitParts[splitParts.size - 1].split(",")[0], 16).toLong()
        }
    }

    private fun convertIfNeeded(value: Double): String? {
        if (!display.enableScript || display.customScript == "" || parseError) return null
        if (expression == null) {
            expression = Expression(display.customScript, evalConfig)
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

    fun sendNotifyUpdate() {
        notifyUpdate?.let { it(this) }
    }

    fun stopRefreshing(isDestroying: Boolean = false) {
        refreshTimer?.cancel(true)
        refreshTimer = null
        if (isDestroying) {
            notifyUpdate = null
        }
    }

}