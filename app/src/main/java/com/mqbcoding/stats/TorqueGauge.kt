package com.mqbcoding.stats

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.github.anastr.speedviewlib.Gauge
import com.github.anastr.speedviewlib.RaySpeedometer
import com.github.anastr.speedviewlib.Speedometer
import com.github.anastr.speedviewlib.components.Indicators.ImageIndicator
import com.github.anastr.speedviewlib.components.Indicators.Indicator
import java.util.Locale


class TorqueGauge : Fragment(){

    private var TAG = "TorqueGauge"
    private var rootView: View? = null
    private var mClock: Speedometer? = null
    private var mRayClock: RaySpeedometer? = null
    private var mGraphValue: TextView? = null
    private var mTextMax: TextView? = null
    private var mTextTitle: TextView? = null
    private var mIcon: TextView? = null
    private var mMax: Speedometer? =null

    private var ticksOn: Boolean? = null
    private var raysOn: Boolean? = null
    private var maxMarksOn: Boolean? = null
    private var maxOn: Boolean? = null
    private var selectedTheme: String? = null
    private var torqueMin = 0
    private var torqueMax = 100

    private var pressureUnit = "bar"
    private var pressureMin = 0
    private var pressureMax = 0
    private var temperatureUnit = "f"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView")
        val view = inflater.inflate(R.layout.fragment_gauge, container, false)
        mClock = view.findViewById(R.id.dial)
        mRayClock = view.findViewById(R.id.ray)
        mGraphValue = view.findViewById(R.id.graphValue)
        mTextMax = view.findViewById(R.id.textMax)
        mTextTitle = view.findViewById(R.id.textTitle)
        mIcon = view.findViewById(R.id.textIcon)
        mMax = view.findViewById(R.id.dial_Max)
        rootView = view
        return rootView
    }

    fun setupTypeface(typeface: Typeface) {
        mClock!!.speedTextTypeface = typeface
        mGraphValue!!.typeface = typeface
        mTextMax!!.typeface = typeface
        mTextTitle!!.typeface = typeface
        mIcon!!.typeface = typeface
    }

    fun turnMinMaxMarksEnabled(enabled: Boolean) {
        //show clock marks for max/min, according to the setting
        maxMarksOn = enabled
        mMax!!.visibility =
            if (enabled) View.VISIBLE else View.INVISIBLE
    }

    fun turnMinMaxTextViewsEnabled(enabled: Boolean) {
        maxOn = enabled
        mTextMax!!.visibility =
            if (enabled) View.VISIBLE else View.INVISIBLE
    }

    fun turnRaysEnabled(enabled: Boolean) {
        raysOn = enabled
        mRayClock!!.visibility = if (enabled) View.VISIBLE else View.INVISIBLE
        if (enabled) {
            //also hide the needle on the clocks
            mRayClock!!.setIndicator(Indicator.Indicators.NoIndicator)
        }
        setupIndicators()
    }

    private fun setupIndicators() {
        var clockSize = mClock!!.height
        if (clockSize == 0) {
            clockSize = 250
        }
        //this is to enable an image as indicator.
        val typedArray = requireContext().theme.obtainStyledAttributes(intArrayOf(R.attr.themedNeedle))
        val resourceId = typedArray.getResourceId(0, 0)
        typedArray.recycle()
        val imageIndicator = ImageIndicator(requireContext(), resourceId, clockSize, clockSize)
        val color = mClock!!.indicatorColor
        Log.i(TAG, "IndicatorColor: $color")
        if (color == 1996533487) {       // if indicator color in the style is @color:aqua, make it an imageindicator
            mClock!!.setIndicator(imageIndicator)
            mRayClock!!.indicatorLightColor = Color.parseColor("#00FFFFFF")
        } else {
            //mClockLeft.setIndicator(Indicator.Indicators.HalfLineIndicator);
            //mClockCenter.setIndicator(Indicator.Indicators.HalfLineIndicator);
            //mClockRight.setIndicator(Indicator.Indicators.HalfLineIndicator);

            // do something to get the other type of indicator
        }

        // if rays on, turn off everything else.
        // it doesn't look too efficient at the moment, but that's to prevent the theme from adding an indicator to the rays.
        if (raysOn!!) {
            // todo: move this to setupClock
            mClock!!.setIndicator(Indicator.Indicators.NoIndicator)
            mRayClock!!.setIndicator(Indicator.Indicators.NoIndicator)

            //make indicatorlight color transparent if you don't need it:
            mClock!!.indicatorLightColor = Color.parseColor("#00FFFFFF")
            //
            mRayClock!!.indicatorLightColor = Color.parseColor("#00FFFFFF")
        } else if (color == -14575885) {
            //if theme has transparent indicator color, give clocks a custom image indicator
            //todo: do this on other fragments as well
            mClock!!.setIndicator(imageIndicator)
            mClock!!.indicatorLightColor = Color.parseColor("#00FFFFFF")
            mRayClock!!.indicatorLightColor = Color.parseColor("#00FFFFFF")
        }
    }

    fun turnTickEnabled(enabled: Boolean) {
        ticksOn = enabled
        val tickNum = if (enabled) 9 else 0
        mClock!!.tickNumber = tickNum
        mRayClock!!.tickNumber = tickNum
        mClock!!.textColor = Color.WHITE
        mRayClock!!.textColor = Color.WHITE
    }


    fun setupClock(data: TorqueData) {
        torqueMin = data.display.minValue
        torqueMax = data.display.maxValue

        data.notifyUpdate = this::onUpdate

        val typedArray2 =
            requireContext().theme.obtainStyledAttributes(intArrayOf(R.attr.themedStopWatchBackground))
        val swBackgroundResource = typedArray2.getResourceId(0, 0)
        typedArray2.recycle()
        // get min/max values and unit from torque

        if (data.pid == null) {
            setupClock(
                mIcon,
                "ic_none",
                "",
                mClock,
                false,
                "",
                0,
                100,
                "float",
                "float"
            )
        } else {
            setupClock(
                mIcon,
                data.getDrawableName() ?: "ic_none",
                if (data.display.showLabel) data.display.label else "",
                mClock,
                false,
                data.display.unit,
                data.display.minValue,
                data.display.maxValue,
                if (data.display.minValue > 100) "integer" else "float",
                "integer"
            )
        }

        // make the icon appear in the color of unitTextColor
        val iconBackground = mIcon!!.background
        if (iconBackground != null) {
            val iconTint = mClock!!.unitTextColor
            iconBackground.setColorFilter(iconTint, PorterDuff.Mode.SRC_ATOP)
            mIcon!!.background = iconBackground
            mIcon!!.setTextColor(iconTint)
        }

        // bring mins and max's in line with the clock
        val minimum = mClock!!.getMinSpeed()
        val maximum = mClock!!.getMaxSpeed()

        //min.setMinMaxSpeed(minimum, maximum);
        mRayClock!!.setMinMaxSpeed(minimum, maximum)
        mMax!!.setMinMaxSpeed(minimum, maximum)
        turnTickEnabled(ticksOn == true)
    }

    private fun setupClock(
        icon: TextView?,
        iconDrawableName: String,
        iconText: String,
        clock: Speedometer?,
        backgroundWithWarningArea: Boolean,
        unit: String?,
        minspeed: Int,
        maxspeed: Int,
        speedFormat: String,
        tickFormat: String
    ) {
        Log.d(TAG, "icon: $icon iconDrawableName: $iconDrawableName")
        val context = requireContext()
        val resId = resources.getIdentifier(
            if (iconDrawableName == "") "ic_none" else iconDrawableName,
            "drawable",
            context.packageName,
        )
        val iconDrawable = context.getDrawable(resId)
        val resIdEmpty = R.drawable.ic_none
        val typedArray =
            context.theme.obtainStyledAttributes(intArrayOf(R.attr.themedEmptyDialBackground))
        val emptyBackgroundResource = typedArray.getResourceId(0, 0)
        typedArray.recycle()

        // set icon. Clocks that don't need an icon have ic_none as icon
        icon!!.background = iconDrawable
        icon.text = iconText
        clock!!.setUnit(unit!!)
        if (minspeed >= maxspeed) {
            Log.e(TAG, "Maxspeed is not greater than minspeed min:${minspeed} max:${maxspeed}")
        } else {
            clock.setMinMaxSpeed(minspeed.toFloat(), maxspeed.toFloat())
        }
        if (tickFormat === "float") {
            clock.tickTextFormat = Gauge.FLOAT_FORMAT.toInt()
        } else {
            clock.tickTextFormat = Gauge.INTEGER_FORMAT.toInt()
        }


        //dynamically scale the icon_space in case there's only an icon, and no text
        if (iconText != "" && resId == resIdEmpty) {
            val params = icon.layoutParams as ConstraintLayout.LayoutParams
            params.width = 40
            icon.layoutParams = params
        }


        // determine if an empty background, without red warning area is wanted
        if (!backgroundWithWarningArea) {
            clock.setBackgroundResource(emptyBackgroundResource)
        }

        //determine the clock format
        if (speedFormat == "float") {
            clock.speedTextFormat = Gauge.FLOAT_FORMAT.toInt()
        } else if (speedFormat == "integer") {
            clock.speedTextFormat = Gauge.INTEGER_FORMAT.toInt()
        }
        clock.setSpeedAt(0f)
        mMax?.setSpeedAt(0f)
        mTextMax?.text = "0"
    }

    fun onUpdate(data: TorqueData) {
        if (!isVisible || isRemoving) return
        val fVal = data.lastData!!.toFloat()
        mClock?.speedTo(fVal, 250)
        mRayClock?.speedTo(fVal, 250)
        if (maxMarksOn == true) {
            mMax?.setSpeedAt(data.maxValue.toFloat())
        }
        if (maxOn == true) {
            mTextMax?.text = String.format(Locale.US, "%.1f", data.maxValue)
        }
    }
}