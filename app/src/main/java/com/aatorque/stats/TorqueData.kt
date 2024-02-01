package com.aatorque.stats

import android.icu.math.BigDecimal
import android.icu.text.NumberFormat
import androidx.lifecycle.MutableLiveData
import com.aatorque.datastore.Coloring
import com.aatorque.datastore.Display
import com.aatorque.datastore.Operation
import com.ezylang.evalex.BaseException
import com.ezylang.evalex.Expression
import com.ezylang.evalex.data.EvaluationValue
import timber.log.Timber
import java.util.concurrent.ScheduledFuture

class TorqueData(var display: Display) {
    companion object {
        const val PREFIX = "torque_"
        val drawableRegex = Regex("res/drawable/(?<name>.+)\\.[a-z]+")
        val twoPlaces: NumberFormat = NumberFormat.getNumberInstance()
        val intPlaces: NumberFormat = NumberFormat.getIntegerInstance()
    }

    init {
        twoPlaces.maximumFractionDigits = 2
        twoPlaces.minimumFractionDigits = 0
        twoPlaces.isGroupingUsed = true
        intPlaces.isGroupingUsed = true
        twoPlaces.roundingMode = BigDecimal.ROUND_HALF_UP
        twoPlaces.roundingMode = BigDecimal.ROUND_HALF_UP
    }

    var pid: String? = null
        get() {
            return if (display.disabled) null else field
        }
    var minValue: Double = Double.POSITIVE_INFINITY
    var maxValue: Double = Double.NEGATIVE_INFINITY
    private var expression: Expression? = null
    var lastDataStr: String = "-"
    var refreshTimer: ScheduledFuture<*>? = null
    var hasReceivedNonZero = false
    var currentAlarm = MutableLiveData<Coloring?>()

    var notifyUpdate: ((TorqueData) -> Unit)? = null
        set(value) {
            field = value
            value?.let { it(this) }
        }

    var lastData: Double = 0.0
        set(value) {
            val converted = convertValue(value)
            field = converted.first
            lastDataStr = converted.second
            if (field > maxValue) {
                maxValue = field
            }
            if (field < minValue) {
                minValue = field
            }
            updateAlarm(field)
        }

    private fun updateAlarm(field: Double) {
        if (!hasReceivedNonZero) {
            return
        }
        val newValue = display.alarmsList.firstOrNull {
            when (it.operation) {
                Operation.GT -> field > it.value
                Operation.GTE -> field >= it.value
                Operation.EQ -> field == it.value
                Operation.LT -> field < it.value
                Operation.LTE -> field <= it.value
                else -> false
            }
        }
        if (currentAlarm.value?.color != newValue?.color) {
            currentAlarm.postValue(newValue)
        }
    }


    init {
        val value = display.pid
        if (value.startsWith(PREFIX)) {
            pid = value.substring(PREFIX.length)
        }
    }

    private fun convertValue(value: Double): Pair<Double, String> {
        val numberFormatter = if (display.wholeNumbers) intPlaces else twoPlaces
        if (!display.enableScript || display.customScript == "") {
            return Pair(value, try{
                numberFormatter.format(value)
            } catch (ex: IllegalArgumentException) {
                Timber.e("Exception formatting unconverted value $value", ex)
                value.toString()
            })
        }
        if (expression == null) {
            val strExp = display.customScript.replace("[x×]".toRegex(), "*")
            Timber.i("Attempting to make expression: $strExp")
            expression = Expression(strExp)
        }
        return try {
            val result = expression!!.with("a", value).evaluate()
            val asString = if (result.dataType == EvaluationValue.DataType.STRING) {
                result.stringValue
            } else {
                numberFormatter.format(result.numberValue)
            }
            Pair(result.numberValue.toDouble(), asString)
        } catch (ex: Exception) {
            when(ex) {
                is BaseException, is NoSuchElementException, is NumberFormatException -> {
                    Timber.e("Unable to parse", ex)
                    ex.printStackTrace()
                    Pair(0.0, "Error")
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