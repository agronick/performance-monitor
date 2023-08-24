package com.mqbcoding.stats

import android.content.Context
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


    fun setupClock(
        queryClock: String?,
        minValue: Int,
        maxValue: Int,
    ) {
        torqueMin = minValue
        torqueMax = maxValue
        val clock = mClock
        val icon = mIcon
        val ray = mRayClock
        val max = mMax

        //todo: get all the min/max unit stuff for exlap items from schema.json
        val queryTrim: String
        val queryLong = queryClock
        var torqueUnit: String? = ""
        val typedArray2 =
            requireContext().theme.obtainStyledAttributes(intArrayOf(R.attr.themedStopWatchBackground))
        val swBackgroundResource = typedArray2.getResourceId(0, 0)
        typedArray2.recycle()
        queryTrim = if (queryClock!!.contains("-")) {
            queryClock.substring(0, queryClock.indexOf("-")) // check the prefix
        } else {
            "other"
        }
        // get min/max values and unit from torque

        when (queryLong) {
            "none" -> setupClock(icon, "ic_none", "", clock, false, "", 0, 100, "float", "float")
            "test" -> setupClock(
                icon,
                "ic_measurement",
                "",
                clock,
                false,
                getString(R.string.testing),
                0,
                360,
                "float",
                "integer"
            )

            "exlap-vehicleSpeed", "torque-speed_0x0d" -> setupClock(
                icon, "ic_none", "", clock, false, getString(
                    R.string.unit_kmh
                ), 0, 300, "integer", "integer"
            )

            "exlap-Nav_Altitude" -> setupClock(
                icon,
                "ic_altitude",
                "",
                clock,
                false,
                "m",
                -100,
                3000,
                "integer",
                "integer"
            )

            "exlap-Nav_Heading" -> {
                setupClock(icon, "ic_heading", "", clock, false, "°", 0, 360, "integer", "integer")
                clock!!.markColor = Color.parseColor("#00FFFFFF")

                //set the degrees so it functions as a circle
                clock.setStartDegree(270)
                clock.setEndDegree(630)
                ray!!.setStartDegree(270)
                ray.setEndDegree(630)
                //min.setStartDegree(270);
                //min.setEndDegree(630);
                max!!.setStartDegree(270)
                max!!.setEndDegree(630)
                // set background resource to the same as stopwatch
                clock.setBackgroundResource(swBackgroundResource)
            }

            "exlap-engineSpeed", "torque-rpm_0x0c" -> {
                setupClock(
                    icon,
                    "ic_none",
                    getString(R.string.unit_rpm),
                    clock,
                    true,
                    getString(R.string.unit_rpm1000),
                    0,
                    9,
                    "float",
                    "integer"
                )
                clock!!.setTicks()
                clock.tickTextFormat = 0
            }

            "torque-voltage_0xff1238", "exlap-batteryVoltage", "torque-voltagemodule_0x42" -> setupClock(
                icon, "ic_battery", "", clock, false, getString(
                    R.string.unit_volt
                ), 0, 17, "float", "integer"
            )

            "exlap-oilTemperature", "torque-oiltemperature_0x5c" -> setupClock(
                icon,
                "ic_oil",
                "",
                clock,
                true,
                "°",
                0,
                200,
                "float",
                "integer"
            )

            "exlap-coolantTemperature", "torque-enginecoolanttemp_0x05" -> setupClock(
                icon,
                "ic_water",
                "",
                clock,
                true,
                "°",
                0,
                200,
                "float",
                "integer"
            )

            "exlap-outsideTemperature", "torque-ambientairtemp_0x46" -> setupClock(
                icon,
                "ic_outsidetemperature",
                "",
                clock,
                false,
                "°",
                -25,
                50,
                "float",
                "integer"
            )

            "torque-transmissiontemp_0x0105", "torque-transmissiontemp_0xfe1805", "exlap-gearboxOilTemperature" -> setupClock(
                icon,
                "ic_gearbox",
                "",
                clock,
                false,
                "°",
                0,
                200,
                "float",
                "integer"
            )

            "torque-turboboost_0xff1202" -> setupClock(
                icon,
                "ic_turbo",
                "",
                clock,
                true,
                torqueUnit,
                torqueMin,
                torqueMax,
                "float",
                "float"
            )

            "exlap-absChargingAirPressure", "exlap-relChargingAirPressure" -> setupClock(
                icon,
                "ic_turbo",
                "",
                clock,
                true,
                pressureUnit,
                pressureMin,
                pressureMax,
                "float",
                "integer"
            )

            "exlap-lateralAcceleration" -> setupClock(
                icon, "ic_lateral", "", clock, false, getString(
                    R.string.unit_g
                ), -3, 3, "float", "float"
            )

            "exlap-longitudinalAcceleration" -> setupClock(
                icon, "ic_longitudinal", "", clock, false, getString(
                    R.string.unit_g
                ), -3, 3, "float", "float"
            )

            "exlap-yawRate" -> setupClock(
                icon,
                "ic_yaw",
                "",
                clock,
                false,
                "°/s",
                -1,
                1,
                "float",
                "integer"
            )

            "wheelAngle" -> setupClock(
                icon,
                "ic_wheelangle",
                "",
                clock,
                false,
                "°",
                -45,
                45,
                "float",
                "integer"
            )

            "exlap-EcoHMI_Score.AvgShort", "exlap-EcoHMI_Score.AvgTrip" -> setupClock(
                icon,
                "ic_eco",
                "",
                clock,
                false,
                "",
                0,
                100,
                "integer",
                "integer"
            )

            "exlap-powermeter" -> setupClock(
                icon,
                "ic_powermeter",
                "",
                clock,
                false,
                "%",
                -1000,
                5000,
                "integer",
                "integer"
            )

            "exlap-acceleratorPosition" -> setupClock(
                icon,
                "ic_pedalposition",
                "",
                clock,
                false,
                "%",
                0,
                100,
                "integer",
                "integer"
            )

            "exlap-brakePressure" -> setupClock(
                icon,
                "ic_brakepedalposition",
                "",
                clock,
                false,
                "%",
                0,
                100,
                "integer",
                "integer"
            )

            "exlap-currentTorque" -> setupClock(
                icon,
                "ic_none",
                "",
                clock,
                false,
                getString(R.string.unit_nm),
                0,
                500,
                "integer",
                "integer"
            )

            "exlap-currentOutputPower" -> setupClock(
                icon,
                "ic_none",
                "",
                clock,
                false,
                getString(R.string.unit_kw),
                0,
                500,
                "integer",
                "integer"
            )

            "exlap-currentConsumptionPrimary", "exlap-cycleConsumptionPrimary" -> setupClock(
                icon,
                "ic_fuelprimary",
                "",
                clock,
                false,
                "l/100km",
                0,
                100,
                "float",
                "integer"
            )

            "exlap-currentConsumptionSecondary", "exlap-cycleConsumptionSecondary" -> setupClock(
                icon,
                "ic_fuelsecondary",
                "",
                clock,
                false,
                "l/100km",
                0,
                100,
                "float",
                "integer"
            )

            "exlap-tankLevelPrimary", "torque-fuellevel_0x2f" -> setupClock(
                icon,
                "ic_fuelprimary",
                "",
                clock,
                false,
                "l",
                0,
                100,
                "float",
                "integer"
            )

            "exlap-tankLevelSecondary" -> setupClock(
                icon,
                "ic_fuelsecondary",
                "",
                clock,
                false,
                "%",
                0,
                100,
                "float",
                "integer"
            )

            "torque-fuelpressure_0x0a" -> setupClock(
                icon,
                "ic_fuelpressure",
                getString(R.string.label_fuel),
                clock,
                false,
                torqueUnit,
                torqueMin,
                torqueMax,
                "float",
                "integer"
            )

            "torque-engineload_0x04", "torque-engineloadabsolute_0x43" -> setupClock(
                icon, "ic_none", getString(
                    R.string.label_load
                ), clock, false, torqueUnit, 0, 100, "float", "integer"
            )

            "torque-timing_advance_0x0e" -> setupClock(
                icon,
                "ic_timing",
                "",
                clock,
                false,
                torqueUnit,
                torqueMin,
                torqueMax,
                "float",
                "integer"
            )

            "torque-intake_air_temperature_0x0f" -> setupClock(
                icon,
                "ic_none",
                getString(R.string.label_iat),
                clock,
                false,
                torqueUnit,
                0,
                100,
                "float",
                "integer"
            )

            "torque-mass_air_flow_0x10" -> setupClock(
                icon,
                "ic_none",
                getString(R.string.label_maf),
                clock,
                false,
                torqueUnit,
                torqueMin,
                torqueMax,
                "float",
                "integer"
            )

            "torque-AFR_0xff1249" -> setupClock(
                icon,
                "ic_none",
                getString(R.string.label_afr),
                clock,
                false,
                torqueUnit,
                0,
                35,
                "float",
                "integer"
            )

            "torque-AFRc_0xff124d" -> setupClock(
                icon,
                "ic_none",
                getString(R.string.label_afrc),
                clock,
                false,
                torqueUnit,
                0,
                35,
                "float",
                "integer"
            )

            "torque-fueltrimshortterm1_0x06" -> setupClock(
                icon,
                "ic_none",
                getString(R.string.label_ftst1),
                clock,
                false,
                torqueUnit,
                -20,
                20,
                "float",
                "integer"
            )

            "torque-fueltrimlongterm1_0x07" -> setupClock(
                icon,
                "ic_none",
                getString(R.string.label_ftlt1),
                clock,
                false,
                torqueUnit,
                -20,
                20,
                "float",
                "integer"
            )

            "torque-fueltrimshortterm2_0x08" -> setupClock(
                icon,
                "ic_none",
                getString(R.string.label_ftst2),
                clock,
                false,
                torqueUnit,
                -20,
                20,
                "float",
                "integer"
            )

            "torque-fueltrimlongterm2_0x09" -> setupClock(
                icon,
                "ic_none",
                getString(R.string.label_ftlt2),
                clock,
                false,
                torqueUnit,
                -20,
                20,
                "float",
                "integer"
            )

            "torque-accelerometer_total_0xff1223" -> setupClock(
                icon,
                "ic_none",
                "",
                clock,
                false,
                "G",
                -3,
                3,
                "float",
                "float"
            )

            "torque-phonebatterylevel_0xff129a" -> setupClock(
                icon,
                "ic_phone",
                "",
                clock,
                false,
                "%",
                0,
                100,
                "integer",
                "integer"
            )

            "torque-phonebarometer_0xff1270" -> setupClock(
                icon,
                "ic_barometer",
                "",
                clock,
                false,
                torqueUnit,
                torqueMin,
                torqueMax,
                "float",
                "integer"
            )

            "torque-obdadaptervoltage_0xff1238" -> setupClock(
                icon,
                "ic_obd2",
                "",
                clock,
                false,
                torqueUnit,
                0,
                17,
                "float",
                "integer"
            )

            "torque-hybridbattlevel_0x5b" -> setupClock(
                icon,
                "ic_battery",
                "",
                clock,
                false,
                "%",
                0,
                100,
                "float",
                "integer"
            )

            "torque-commandedequivalenceratiolambda_0x44" -> setupClock(
                icon,
                "ic_none",
                "lambda",
                clock,
                false,
                torqueUnit,
                0,
                3,
                "float",
                "float"
            )

            "torque-catalysttemperature_0x3c" -> setupClock(
                icon,
                "ic_catalyst",
                "",
                clock,
                false,
                torqueUnit,
                0,
                1000,
                "float",
                "integer"
            )

            "torque-relativethrottleposition_0x45", "torque-absolutethrottlepostion_0x47", "torque-throttle_position_0x11" -> setupClock(
                icon,
                "ic_throttle",
                "",
                clock,
                false,
                torqueUnit,
                0,
                100,
                "float",
                "integer"
            )

            "torque-intakemanifoldpressure_0x0b" -> setupClock(
                icon,
                "ic_manifold",
                "",
                clock,
                false,
                torqueUnit,
                0,
                200,
                "float",
                "integer"
            )

            "torque-chargeaircoolertemperature_0x77" -> setupClock(
                icon,
                "ic_cact",
                "",
                clock,
                false,
                torqueUnit,
                0,
                100,
                "float",
                "integer"
            )

            "torque-pressurecontrol_0x70" -> setupClock(
                icon,
                "ic_turbo",
                "",
                clock,
                false,
                pressureUnit,
                pressureMin * 30,
                pressureMax * 30,
                "float",
                "integer"
            )

            "torque-o2sensor1equivalenceratio_0x34" -> setupClock(
                icon,
                "ic_none",
                "O2 sensor",
                clock,
                false,
                torqueUnit,
                0,
                3,
                "float",
                "float"
            )

            "exlap-tyrePressures.pressureRearRight" -> setupClock(
                icon,
                "ic_tyre",
                getString(R.string.label_tyreRR),
                clock,
                false,
                pressureUnit,
                0,
                4,
                "float",
                "float"
            )

            "exlap-tyrePressures.pressureRearLeft" -> setupClock(
                icon,
                "ic_tyre",
                getString(R.string.label_tyreRL),
                clock,
                false,
                pressureUnit,
                0,
                4,
                "float",
                "float"
            )

            "exlap-tyrePressures.pressureFrontRight" -> setupClock(
                icon,
                "ic_tyre",
                getString(R.string.label_tyreFR),
                clock,
                false,
                pressureUnit,
                0,
                4,
                "float",
                "float"
            )

            "exlap-tyrePressures.pressureFrontLeft" -> setupClock(
                icon,
                "ic_tyre",
                getString(R.string.label_tyreFL),
                clock,
                false,
                pressureUnit,
                0,
                4,
                "float",
                "float"
            )

            "exlap-tyreTemperatures.temperatureRearRight" -> setupClock(
                icon,
                "ic_tyre",
                getString(R.string.label_tyreRR),
                clock,
                false,
                temperatureUnit,
                0,
                100,
                "float",
                "integer"
            )

            "exlap-tyreTemperatures.temperatureRearLeft" -> setupClock(
                icon,
                "ic_tyre",
                getString(R.string.label_tyreRL),
                clock,
                false,
                temperatureUnit,
                0,
                100,
                "float",
                "integer"
            )

            "exlap-tyreTemperatures.temperatureFrontRight" -> setupClock(
                icon, "ic_tyre", getString(
                    R.string.label_tyreFR
                ), clock, false, temperatureUnit, 0, 100, "float", "integer"
            )

            "exlap-tyreTemperatures.temperatureFrontLeft" -> setupClock(
                icon,
                "ic_tyre",
                getString(R.string.label_tyreFL),
                clock,
                false,
                temperatureUnit,
                0,
                100,
                "float",
                "integer"
            )

            "torque-exhaustgastempbank1sensor1_0x78" -> setupClock(
                icon,
                "ic_exhaust",
                "1",
                clock,
                false,
                torqueUnit,
                0,
                1000,
                "float",
                "integer"
            )

            "torque-exhaustgastempbank1sensor2_0xff1282" -> setupClock(
                icon,
                "ic_exhaust",
                "2",
                clock,
                false,
                torqueUnit,
                0,
                1000,
                "float",
                "integer"
            )

            "torque-exhaustgastempbank1sensor3_0xff1283" -> setupClock(
                icon,
                "ic_exhaust",
                "3",
                clock,
                false,
                torqueUnit,
                0,
                1000,
                "float",
                "integer"
            )

            "torque-exhaustgastempbank1sensor4_0xff1284" -> setupClock(
                icon,
                "ic_exhaust",
                "4",
                clock,
                false,
                torqueUnit,
                0,
                1000,
                "float",
                "integer"
            )

            "torque-fuelrailpressure_0x23" -> setupClock(
                icon,
                "ic_fuelpressure",
                "",
                clock,
                false,
                torqueUnit,
                0,
                100,
                "float",
                "integer"
            )
        }

        // make the icon appear in the color of unitTextColor
        val iconBackground = icon!!.background
        if (iconBackground != null) {
            val iconTint = clock!!.unitTextColor
            iconBackground.setColorFilter(iconTint, PorterDuff.Mode.SRC_ATOP)
            icon.background = iconBackground
            icon.setTextColor(iconTint)
        }

        // bring mins and max's in line with the clock
        val minimum = clock!!.getMinSpeed()
        val maximum = clock.getMaxSpeed()

        //min.setMinMaxSpeed(minimum, maximum);
        ray!!.setMinMaxSpeed(minimum, maximum)
        max!!.setMinMaxSpeed(minimum, maximum)
        mClock!!.setSpeedAt(50f)
        mRayClock!!.setSpeedAt(50f)
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
        val resId = resources.getIdentifier(iconDrawableName, "drawable", context.packageName)
        val iconDrawable = context.getDrawable(resId)
        val resIdEmpty = resources.getIdentifier("ic_none", "drawable", context.packageName)
        val typedArray =
            context.theme.obtainStyledAttributes(intArrayOf(R.attr.themedEmptyDialBackground))
        val emptyBackgroundResource = typedArray.getResourceId(0, 0)
        typedArray.recycle()

        // set icon. Clocks that don't need an icon have ic_none as icon
        icon!!.background = iconDrawable
        icon.text = iconText
        clock!!.setUnit(unit!!)
        clock.setMinMaxSpeed(minspeed.toFloat(), maxspeed.toFloat())
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
    }
}