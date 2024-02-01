package com.aatorque.stats

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toRect
import com.aatorque.datastore.Coloring
import com.aatorque.utils.OpenCloseAnimator
import com.github.anastr.speedviewlib.ImageSpeedometer

class TorqueSpeedometer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ImageSpeedometer(context, attrs, defStyleAttr) {

    var icon: Drawable? = null
    var alarmPaint: Paint? = null
    val alarmBottomOffset = dpTOpx(3f)
    val alarmAnimator = OpenCloseAnimator.ofFloat(0f, 1f).apply {
        addUpdateListener {
            invalidate()
        }
        duration = 300
    }

    fun setAlarm(coloring: Coloring?) {
        alarmAnimator.setState(coloring != null)
        coloring?.let {
            alarmPaint = Paint().apply {
                color = it.color
                style = Paint.Style.STROKE
                strokeWidth = alarmBottomOffset
                setShadowLayer(
                    dpTOpx(3f),
                    0f,
                    0f,
                    Color(0f, 0f, 0f, 0.3f).toArgb()
                )
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawIcon(canvas)
        alarmPaint?.let{
            var start = 45f
            var distance = 90f
            val percent = alarmAnimator.animatedValue as Float
            start += start - (start * percent)
            distance *= percent
            canvas.drawArc(
                RectF(
                    0f + alarmBottomOffset,
                    0f + alarmBottomOffset,
                    size.toFloat() - alarmBottomOffset,
                    size.toFloat() - alarmBottomOffset
                ),
                start,
                distance,
                false,
                it,
            )
        }
    }

    val iconSize = dpTOpx(35f)
    val halfIcon = (iconSize * 0.5f)
    val squeezeTop = dpTOpx(7f)
    val squeezeBottom = dpTOpx(3f)

    override fun getSpeedUnitTextBounds(): RectF {
        return super.getSpeedUnitTextBounds().apply {
            if (icon != null) {
                top -= halfIcon
                bottom -= halfIcon
                top += squeezeTop
                bottom += squeezeTop
            }
        }
    }

    private fun getIconBounds(): RectF {
        val bounds = getSpeedUnitTextBounds()
        val minPoint = (bounds.left + ((bounds.right - bounds.left) * 0.5f))

        return RectF(
            minPoint - halfIcon,
            bounds.bottom - squeezeBottom,
            minPoint + halfIcon,
            bounds.bottom + iconSize - squeezeBottom,
        )
    }

    private fun drawIcon(canvas: Canvas) {
        icon?.let{
            it.bounds = getIconBounds().toRect()
            it.draw(canvas)
        }
    }
}
