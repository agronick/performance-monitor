package com.aatorque.stats

import android.content.res.Configuration
import android.content.res.Resources.NotFoundException
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import com.aatorque.datastore.MaxControl
import com.aatorque.stats.databinding.FragmentGaugeBinding
import com.github.anastr.speedviewlib.ImageSpeedometer
import com.github.anastr.speedviewlib.RaySpeedometer
import com.github.anastr.speedviewlib.Speedometer
import com.github.anastr.speedviewlib.components.Section
import com.github.anastr.speedviewlib.components.indicators.Indicator
import com.github.anastr.speedviewlib.components.indicators.TriangleIndicator
import timber.log.Timber
import java.util.Locale
import kotlin.math.abs


class TorqueGauge : Fragment() {

    private var rootView: View? = null
    private lateinit var mClock: ImageSpeedometer
    private lateinit var mRayClock: RaySpeedometer
    private lateinit var mGraphValue: TextView
    private lateinit var mTextMax: TextView
    private lateinit var mTextTitle: TextView
    private lateinit var mIcon: TextView
    private lateinit var mMax: Speedometer

    private var rayOn = false
    private lateinit var binding: FragmentGaugeBinding

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("ticksOn", binding.ticksOn == true)
        outState.putInt("maxMarksOn", binding.showLimitMarked?.number ?: MaxControl.OFF_VALUE)
        outState.putInt("maxOn", binding.showLimitBelow?.number ?: MaxControl.OFF_VALUE)
        outState.putFloat("torqueMin", binding.minValue)
        outState.putFloat("torqueMax", binding.maxValue)
        outState.putBoolean("rayOn", rayOn)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.i("onCreateView")
        binding = FragmentGaugeBinding.inflate(inflater, container, false)
        val view = binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val state = savedInstanceState ?: Bundle()
        mMax.indicator = TriangleIndicator(requireContext())
        mMax.indicator.color = requireContext().theme.obtainStyledAttributes(intArrayOf(R.attr.themedNeedleColor)).getColor(0, Color.RED)
        setMinMax(
            state.getFloat("minValue", 0f).toInt(),
            state.getFloat("maxValue", 100f).toInt()
        )
        turnMinMaxMarksEnabled(
            MaxControl.forNumber(
                state.getInt(
                    "maxMarksOn",
                    MaxControl.OFF_VALUE
                )
            )
        )
        turnMinMaxTextViewsEnabled(
            MaxControl.forNumber(
                state.getInt(
                    "maxOn",
                    MaxControl.OFF_VALUE
                )
            )
        )
        turnRaysEnabled(state.getBoolean("rayOn", false))
        turnTickEnabled(state.getBoolean("ticksOn", false))
        mMax.clearSections()
        mMax.addSections(
            Section(
                0f, 1f,
                android.R.color.transparent
            )
        )
        mRayClock.clearSections()
        mRayClock.addSections(
            Section(
                0f, 1f,
                requireContext().theme.obtainStyledAttributes(intArrayOf(R.attr.themedNeedleColor))
                    .getColor(
                        0, Color.WHITE
                    )
            )
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mClock.invalidate()
        mRayClock.invalidate()
    }

    fun setupTypeface(typeface: Typeface) {
        binding.font = typeface
    }

    fun turnMinMaxMarksEnabled(enabled: MaxControl) {
        binding.showLimitMarked = enabled
    }

    fun turnMinMaxTextViewsEnabled(enabled: MaxControl) {
        binding.showLimitBelow = enabled
    }

    fun turnTickEnabled(enabled: Boolean) {
        binding.ticksOn = enabled
    }

    fun turnRaysEnabled(enabled: Boolean) {
        rayOn = enabled
        mRayClock.visibility = if (enabled) View.VISIBLE else View.INVISIBLE
        if (enabled) {
            //also hide the needle on the clocks
            mRayClock.setIndicator(Indicator.Indicators.NoIndicator)
        }

        //this is to enable an image as indicator.
        val indicatorDrawable =
            requireContext().theme.obtainStyledAttributes(intArrayOf(R.attr.themedNeedle))
                .getDrawable(0)
        val imageIndicator = SizedImageIndicator(requireContext(), indicatorDrawable!!)
        val color = mClock.indicator.color
        Timber.i("IndicatorColor: $color")
        if (color == 1996533487) {       // if indicator color in the style is @color:aqua, make it an imageindicator
            mClock.indicator = imageIndicator
            mRayClock.indicatorLightColor = Color.parseColor("#00FFFFFF")
        } else {
            //mClockLeft.setIndicator(Indicator.Indicators.HalfLineIndicator);
            //mClockCenter.setIndicator(Indicator.Indicators.HalfLineIndicator);
            //mClockRight.setIndicator(Indicator.Indicators.HalfLineIndicator);

            // do something to get the other type of indicator
        }

        // if rays on, turn off everything else.
        // it doesn't look too efficient at the moment, but that's to prevent the theme from adding an indicator to the rays.
        if (enabled) {
            mClock.setIndicator(Indicator.Indicators.NoIndicator)
            mRayClock.setIndicator(Indicator.Indicators.NoIndicator)

            //make indicatorlight color transparent if you don't need it:
            mClock.indicatorLightColor = Color.parseColor("#00FFFFFF")
            //
            mRayClock.indicatorLightColor = Color.parseColor("#00FFFFFF")
        } else if (color == -14575885) {
            //if theme has transparent indicator color, give clocks a custom image indicator
            //todo: do this on other fragments as well
            mClock.indicator = imageIndicator
            mClock.indicatorLightColor = Color.parseColor("#00FFFFFF")
            mRayClock.indicatorLightColor = Color.parseColor("#00FFFFFF")
        }
    }

    fun setupClock(data: TorqueData) {
        data.notifyUpdate = this::onUpdate
        val iconDrawableName = data.getDrawableName() ?: "ic_none"
        val iconText = if (data.display.showLabel) data.display.label else ""

        // get min/max values and unit from torque
        val context = requireContext()

        val resId = try {
            resources.getIdentifier(
                if (iconDrawableName == "") "ic_none" else iconDrawableName,
                "drawable",
                context.packageName,
            )
        } catch (e: NotFoundException) {
            R.drawable.ic_none
        }

        // set mIcon. mClocks that don't need an mIcon have ic_none as mIcon
        mIcon.setBackgroundResource(resId)
        mTextTitle.text = iconText
        mClock.unit = data.display.unit

        //dynamically scale the mIcon_space in case there's only an mIcon, and no text
        if (iconText != "" && resId == R.drawable.ic_none) {
            val params = mIcon.layoutParams as ConstraintLayout.LayoutParams
            params.width = 40
            mIcon.layoutParams = params
        }


        // make the icon appear in the color of unitTextColor
        val iconBackground = mIcon.background
        if (iconBackground != null) {
            val iconTint = mClock.unitTextColor
            iconBackground.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    iconTint,
                    BlendModeCompat.SRC_ATOP,
                )
            mIcon.background = iconBackground
            mIcon.setTextColor(iconTint)
        }

        setMinMax(data.display.minValue, data.display.maxValue)
        mClock.setSpeedAt(mClock.minSpeed)
        mMax.setSpeedAt(mMax.minSpeed)
        mRayClock.setSpeedAt(mRayClock.minSpeed)
        binding.limitValue = "-"
        turnTickEnabled(data.display.ticksActive)
        turnMinMaxMarksEnabled(data.display.maxMarksActive)
        turnMinMaxTextViewsEnabled(data.display.maxValuesActive)
        turnRaysEnabled(data.display.highVisActive)
    }

    private fun setMinMax(minspeed: Int, maxspeed: Int) {
        val minimum = minspeed.toFloat()
        var maximum = maxspeed.toFloat()
        if (minspeed > maxspeed) {
            Timber.e("Maxspeed is not greater than minspeed min:${minspeed} max:${maxspeed}")
        } else if (minimum == maximum) {
            Timber.e("Maxspeed is equal to minspeed min:${minspeed} max:${maxspeed}")
            maximum += 1f
        }
        binding.minLimit = minimum.coerceAtMost(maximum)
        binding.maxLimit = maximum.coerceAtLeast(minimum)
        val format = if (
            // -1 because 0 is a tick
            (binding.maxLimit - abs(binding.minLimit)) < (mClock.tickNumber - 1)
        ) "%.1f" else "%.0f"
        val locale = Locale.getDefault()
        mClock.onPrintTickLabel = { _, speed -> format.format(locale, speed) }
    }

    private fun onUpdate(data: TorqueData) {
        val fVal = data.lastData.toFloat()
        mClock.speedTo(fVal, TorqueRefresher.REFRESH_INTERVAL)
        mRayClock.speedTo(fVal, TorqueRefresher.REFRESH_INTERVAL)
        if (data.display.maxMarksActive == MaxControl.MAX && data.maxValue.isFinite()) {
            mMax.setSpeedAt(data.maxValue.toFloat())
            Timber.d("Settings max speed ${data.maxValue}")
        } else if (data.display.maxMarksActive == MaxControl.MIN && data.minValue.isFinite()) {
            mMax.setSpeedAt(data.minValue.toFloat())
            Timber.d("Settings min speed ${data.minValue}")
        }
        if (data.display.maxValuesActive != MaxControl.OFF) {
            val possibleValue =
                if (data.display.maxValuesActive == MaxControl.MAX) data.maxValue else data.minValue
            if (possibleValue.isFinite()) {
                binding.limitValue = "%.${
                    if (possibleValue >= 1000 || data.display.wholeNumbers) '0' else '1'
                }f".format(
                    Locale.getDefault(),
                    possibleValue
                )
            }
        }
    }
}