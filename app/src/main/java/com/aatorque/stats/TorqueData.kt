package com.aatorque.stats

import com.ezylang.evalex.BaseException
import com.ezylang.evalex.Expression
import com.aatorque.datastore.Display
import timber.log.Timber
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.concurrent.ScheduledFuture

class TorqueData(val display: Display) {

    var pid: String? = null
    var minValue: Double = Double.POSITIVE_INFINITY
    var maxValue: Double = Double.NEGATIVE_INFINITY
    private var expression: Expression? = null
    var lastDataStr: String? = null
    var refreshTimer: ScheduledFuture<*>? = null
    var hasReceivedNonZero = false
    val dfDefault = DecimalFormat("#.##")
    val dfRound = DecimalFormat("#")

    init {
        dfDefault.roundingMode = RoundingMode.HALF_UP
        dfRound.roundingMode = RoundingMode.HALF_UP
    }

    var notifyUpdate: ((TorqueData) -> Unit)? = null
        set(value) {
            field = value
            value?.let { it(this) }
        }

    var lastData: Double = 0.0
        set(value) {
            lastDataStr = convertIfNeeded(value)
            field = lastDataStr?.let {
                try {
                    it.toDouble()
                } catch (e: NumberFormatException) {
                    field
                }
            } ?: value
            if (field > maxValue) {
                maxValue = field
            }
            if (field < minValue) {
                minValue = field
            }
        }
    companion object {
        const val PREFIX = "torque_"
        val drawableRegex = Regex("res/drawable/(?<name>.+)\\.[a-z]+")
    }

    init {
        val value = display.pid
        if (value.startsWith(PREFIX)) {
            pid = value.substring(PREFIX.length)
        }
    }

    private fun convertIfNeeded(value: Double): String? {
        if (!display.enableScript || display.customScript == "") return null
        if (expression == null) {
            expression = Expression(display.customScript.replace("[x×]".toRegex(), "*"))
        }
        return try {
            val result = expression!!.with("a", value).evaluate()
            try {
                val df = if (display.wholeNumbers) {
                    dfRound
                } else {
                    dfDefault
                }
                result.stringValue.toDouble()
                df.format(result.numberValue).toString()
            } catch (e: NumberFormatException) {
                result.stringValue
            }
        } catch (ex: Exception) {
            when(ex) {
                is BaseException, is NoSuchElementException, is NumberFormatException -> {
                    Timber.e("Unable to parse", ex)
                    ex.printStackTrace()
                    "Error"
                }
                else -> throw ex
            }
        }
    }

    fun getDrawableName(): String? {
        val match = drawableRegex.matchEntire(display.icon)
        if (match != null) {
            return match.groups["name"]!!.value
        }
        return display.icon
    }

    fun sendNotifyUpdate() {
        if (notifyUpdate == null) {
            Timber.e("Cannot update, notifyUpdate is null")
        } else {
            notifyUpdate?.let {
                it(this)
            }
        }
    }

    fun stopRefreshing(isDestroying: Boolean = false) {
        refreshTimer?.cancel(true)
        refreshTimer = null
        if (isDestroying) {
            notifyUpdate = null
        }
    }

}