package com.aatorque.stats

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.github.anastr.speedviewlib.components.indicators.ImageIndicator
import com.github.anastr.speedviewlib.components.indicators.Indicator

class SizedImageIndicator
/**
 * create indicator from bitmap, the indicator direction must be up.
 *
 * center indicator position will be center of speedometer.
 * @param context you can use `applicationContext`.
 * @param bitmapIndicator the indicator.
 */
constructor(context: Context, private val bitmapIndicator: Drawable) : Indicator<ImageIndicator>(context) {

    override fun draw(canvas: Canvas) {
        bitmapIndicator.draw(canvas)
    }

    override fun updateIndicator() {
        val size = (speedometer?.size ?: 250)
        bitmapIndicator.setBounds(0, 0, size,  size)
    }

    override fun setWithEffects(withEffects: Boolean) {}
}