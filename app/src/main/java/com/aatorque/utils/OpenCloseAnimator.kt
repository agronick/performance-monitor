package com.aatorque.utils

import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator

class OpenCloseAnimator : ValueAnimator() {

    init {
        repeatCount = 0
        interpolator = DecelerateInterpolator()
    }

    var isOpen = false
    var hasReversed = false
    fun setState(hasValue: Boolean) {
        if (hasValue) {
            if (!isOpen) {
                if (hasReversed && isRunning) {
                    reverse()
                } else {
                    start()
                }
                hasReversed = false
                isOpen = true
            }
            isOpen = true
        } else if (isOpen) {
            reverse()
            isOpen = false
            hasReversed = true
        }
    }

    companion object {
        fun ofFloat(vararg values: Float): OpenCloseAnimator {
            val anim = OpenCloseAnimator()
            anim.setFloatValues(*values)
            return anim
        }
    }

}