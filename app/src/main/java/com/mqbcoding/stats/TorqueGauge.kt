package com.mqbcoding.stats

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.anastr.speedviewlib.RaySpeedometer
import com.github.anastr.speedviewlib.Speedometer
import com.github.anastr.speedviewlib.components.Indicators.ImageIndicator
import com.github.anastr.speedviewlib.components.Indicators.Indicator

class TorqueGauge : FontFragmentBase(){

    private var TAG = "TorqueGauge"
    private var rootView: View? = null
    private var mClock: Speedometer? = null
    private var mRayClock: RaySpeedometer? = null
    private var mGraphValue: TextView? = null
    private var mTextMax: TextView? = null
    private var mTextTitle: TextView? = null
    private var mIcon: TextView? = null

    private var ticksOn: Boolean? = null
    private var raysOn: Boolean? = null
    private var selectedTheme: String? = null

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
        rootView = view
        onPreferencesChangeHandler()
        return rootView
    }

    override fun onPreferencesChangeHandler() {
        super.onPreferencesChangeHandler()
        val sharedPreferences = activity?.getSharedPreferences(
            "shared_preferences",
            Context.MODE_PRIVATE,
        ) ?: return

        //show high visible rays on, according to the setting
        val readedRaysOn = sharedPreferences.getBoolean(
            "highVisActive",
            false
        ) //true = show high vis rays, false = don't show them.
        if (raysOn == null || readedRaysOn != raysOn) {
            raysOn = readedRaysOn
            turnRaysEnabled(raysOn!!)
        }
        val readedTheme = sharedPreferences.getString("selectedTheme", "")
        if (readedTheme != selectedTheme) {
            selectedTheme = readedTheme
            turnRaysEnabled(raysOn!!)
        }
        val readedTicksOn = sharedPreferences.getBoolean(
            "ticksActive",
            false
        ) // if true, it will display the value of each of the ticks
        if (ticksOn == null || readedTicksOn != ticksOn) {
            ticksOn = readedTicksOn
            turnTickEnabled(ticksOn!!)
        }
    }

    override fun setupTypeface(typeface: Typeface) {
        mClock!!.speedTextTypeface = typeface
        mGraphValue!!.typeface = typeface
        mTextMax!!.typeface = typeface
        mTextTitle!!.typeface = typeface
        mIcon!!.typeface = typeface
    }

    private fun turnMinMaxMarksEnabled(enabled: Boolean) {
        //show clock marks for max/min, according to the setting
        mTextMax!!.visibility =
            if (enabled) View.VISIBLE else View.INVISIBLE
    }

    private fun turnMinMaxTextViewsEnabled(enabled: Boolean) {
        mTextMax!!.visibility =
            if (enabled) View.VISIBLE else View.INVISIBLE
    }

    private fun turnRaysEnabled(enabled: Boolean) {
        mRayClock!!.visibility = if (enabled) View.VISIBLE else View.INVISIBLE
        if (enabled) {
            //also hide the needle on the clocks
            mRayClock!!.setIndicator(Indicator.Indicators.NoIndicator)
        } else {
            setupIndicators()
        }
    }

    private fun setupIndicators() {
        var clockSize = mClock!!.height
        if (clockSize == 0) {
            clockSize = 250
        }
        //this is to enable an image as indicator.
        val typedArray = context!!.theme.obtainStyledAttributes(intArrayOf(R.attr.themedNeedle))
        val resourceId = typedArray.getResourceId(0, 0)
        typedArray.recycle()
        val imageIndicator = ImageIndicator(context!!, resourceId, clockSize, clockSize)
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

    private fun turnTickEnabled(enabled: Boolean) {
        val tickNum = 9
        mRayClock!!.tickNumber = if (enabled) tickNum else 0
        mRayClock!!.textColor = Color.WHITE
    }
}