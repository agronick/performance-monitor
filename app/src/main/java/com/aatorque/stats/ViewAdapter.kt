package com.aatorque.stats

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.github.anastr.speedviewlib.Gauge
import com.github.anastr.speedviewlib.ImageSpeedometer
import kotlin.math.roundToInt

@BindingAdapter(
    "layout_constraintTop_toBottomOf",
    "layout_constraintBottom_toTopOf",
    "layout_constraintRight_toLeftOf",
    "layout_constraintTop_toTopOf",
    "layout_constraintLeft_toRightOf",
    "layout_constraintRight_toRightOf",
    "layout_constraintBottom_toBottomOf",
    "layout_constraintLeft_toLeftOf",
    requireAll = false
)
fun setConstraintTopToBottomOf(view: View, topBottom: Int?, bottomTop: Int?, rightLeft: Int?, topTop: Int?, leftRight: Int?, rightRight: Int?, bottomBottom: Int?, leftLeft: Int?) {
    val params = view.layoutParams as ConstraintLayout.LayoutParams
    params.topToBottom = topBottom ?: params.topToBottom
    params.bottomToTop = bottomTop ?: params.bottomToTop
    params.rightToLeft = rightLeft ?: params.rightToLeft
    params.topToTop = topTop ?: params.topToTop
    params.leftToRight = leftRight ?: params.leftToRight
    params.rightToRight = rightRight ?: params.rightToRight
    params.bottomToBottom = bottomBottom ?: params.bottomToBottom
    params.leftToLeft = leftLeft ?: params.leftToLeft
    view.layoutParams = params
    view.requestLayout()
}


@BindingAdapter("tickNumber")
fun setBackground(view: ImageSpeedometer, tickNumber: Int) {
    view.tickNumber = tickNumber
    val typedArray =
        view.context.theme.obtainStyledAttributes(intArrayOf(
            if (tickNumber == 0) R.attr.themedEmptyDialBackground else R.attr.themedDialBackground
        ))
    view.setBackgroundResource(typedArray.getResourceId(0, 0))
}

@BindingAdapter("wholeNumbers")
fun wholeNumbers(view: ImageSpeedometer, wholeNumbers: Boolean) {
    view.speedTextListener = if (wholeNumbers) {
        { speed -> speed.roundToInt().toString() }
    } else {
        { speed -> "%.1f".format(view.locale, speed) }
    }
}

@BindingAdapter("minMax")
fun setMinMax(view: Gauge, minMax: Pair<Float, Float>) {
    view.setMinMaxSpeed(minMax.first, minMax.second)
}

@BindingAdapter("android:layout_height", "android:layout_width", requireAll = false)
fun setLayoutHeight(view: View, height: Int?, width: Int?) {
    val layoutParams: ViewGroup.LayoutParams = view.layoutParams
    if (height != null) {
        layoutParams.height = height as Int
    }
    if (width != null) {
        layoutParams.width = width as Int
    }
    view.layoutParams = layoutParams
}