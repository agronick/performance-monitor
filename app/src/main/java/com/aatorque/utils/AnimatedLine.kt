package com.aatorque.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb


class AnimatedLine(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    var lastRect: RectF? = null
    val animator = OpenCloseAnimator.ofFloat(0f, 1f).apply {
        duration = 250
    }

    var paint: Paint? = null
    var color: Int? = null
        set(value) {
            field = value
            animator.setState(field != null)
            field?.let {
                paint = Paint().apply {
                    color = it
                    style = Paint.Style.FILL
                    strokeWidth = 3f
                    setShadowLayer(
                        3f,
                        0f,
                        0f,
                        Color(0f, 0f, 0f, 0.5f).toArgb()
                    )
                }
            }
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        lastRect?.let { lr ->
            paint?.let {
                canvas.drawRect(
                    lr, it
                )
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        handler.post {
            val midpoint = measuredWidth * 0.5f
            animator.addUpdateListener {
                lastRect = if (it.animatedValue == 0f) {
                    null
                } else {
                    val halfWidthDiff = midpoint * it.animatedValue as Float
                    RectF(
                        midpoint - halfWidthDiff,
                        0f,
                        midpoint + halfWidthDiff,
                        height.toFloat()
                    )
                }
                invalidate()
            }
        }
    }


}