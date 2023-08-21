package com.mqbcoding.stats

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

class TorqueDisplay: Fragment() {
    val TAG = "TorqueDisplay"
    private var rootView: View? = null
    private var valueElement: TextView? = null
    private var iconElement: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView")
        val view = inflater.inflate(R.layout.fragment_display, container, false)
        rootView = view
        valueElement = view.findViewById(R.id.valueElement)
        iconElement = view.findViewById(R.id.iconElement)
        setupElement("none")
        return rootView
    }
    // this sets all the labels/values in an initial state, depending on the chosen options
    fun setupElement(queryElement: String?) {
        val label = iconElement
        val value = valueElement

        if(label == null || value == null) return
        
        context.let {
            context ->  {
                if(context != null) {
                    //set element label/value to default value first
                    label.setBackgroundResource(0)
                    value.visibility = View.VISIBLE
                    value.text = "-"
                    label.text = ""
                    var icon = ""


                    // set items to have a "-" as value.
                    //todo: clean this up. This can be done much nicer.
                    if (queryElement == "none") {
                        label.text = ""
                        value.text = ""
                        icon = "empty"
                        value.visibility = View.INVISIBLE
                    } else {
                        label.text = ""
                        value.text = "-"
                    }
                    when (queryElement) {
                        "none" -> icon = "empty"
                        "test" -> label.background = context.getDrawable(R.drawable.ic_measurement)
                        "batteryVoltage", "torque-voltage_0xff1238" -> {
                            value.text = TorqueData.FORMAT_VOLT0
                            label.background = context.getDrawable(R.drawable.ic_battery)
                        }

                        "Nav_Altitude" -> label.background = context.getDrawable(R.drawable.ic_altitude)
                        "Nav_Heading" -> label.background = context.getDrawable(R.drawable.ic_heading)
                        "coolantTemperature", "torque-enginecoolanttemp_0x05" -> {
                            label.text = ""
                            value.text = TorqueData.FORMAT_TEMPERATURE0
                            label.background = context.getDrawable(R.drawable.ic_water)
                        }

                        "oilTemperature", "torque-oiltemperature_0x5c" -> {
                            value.text = TorqueData.FORMAT_TEMPERATURE0
                            label.background = context.getDrawable(R.drawable.ic_oil)
                        }

                        "vehicleSpeed", "torque-speed_0x0d" -> {
                            label.setText(R.string.unit_kmh)
                            icon = "empty"
                        }

                        "torque-rpm_0x0c", "engineSpeed" -> {
                            label.setText(R.string.unit_rpm)
                            icon = "empty"
                        }

                        "currentOutputPower" -> {
                            label.text = context.getString(R.string.unit_hp)
                               // if (powerUnits!!) context.getString(R.string.unit_kw) else context.getString(R.string.unit_hp)
                            icon = "empty"
                        }

                        "currentTorque" -> {
                            label.setText(R.string.unit_nm)
                            icon = "empty"
                        }

                        "gearboxOilTemperature", "torque-transmissiontemp_0x0105", "torque-transmissiontemp_0xfe1805" -> {
                            value.text = TorqueData.FORMAT_TEMPERATURE0
                            label.background = context.getDrawable(R.drawable.ic_gearbox)
                        }

                        "outsideTemperature", "torque-ambientairtemp_0x46" -> {
                            value.text = "-" //value.setText(R.string.format_temperature0);
                            label.background = context.getDrawable(R.drawable.ic_outsidetemperature)
                        }

                        "currentGear" -> label.background = context.getDrawable(R.drawable.ic_gearbox)
                        "torque-accelerometer_total_0xff1223", "lateralAcceleration" -> label.background =
                            context.getDrawable(
                                R.drawable.ic_lateral
                            )

                        "longitudinalAcceleration" -> label.background =
                            context.getDrawable(R.drawable.ic_longitudinal)

                        "yawRate" -> label.background = context.getDrawable(R.drawable.ic_yaw)
                        "wheelAngle" -> label.background = context.getDrawable(R.drawable.ic_steering)
                        "acceleratorPosition" -> label.background =
                            context.getDrawable(R.drawable.ic_pedalposition)

                        "brakePressure" -> label.background =
                            context.getDrawable(R.drawable.ic_brakepedalposition)

                        "powermeter" -> label.background = context.getDrawable(R.drawable.ic_powermeter)
                        "EcoHMI_Score.AvgShort" -> label.background = context.getDrawable(R.drawable.ic_eco)
                        "EcoHMI_Score.AvgTrip" -> label.background = context.getDrawable(R.drawable.ic_ecoavg)
                        "shortTermConsumptionPrimary" -> label.background =
                            context.getDrawable(R.drawable.ic_fuelprimary)

                        "shortTermConsumptionSecondary" -> label.background =
                            context.getDrawable(R.drawable.ic_fuelsecondary)

                        "Nav_CurrentPosition.Longitude", "Nav_CurrentPosition.Latitude", "Nav_CurrentPosition.City", "Nav_CurrentPosition.State", "Nav_CurrentPosition.Country", "Nav_CurrentPosition.Street" -> label.background =
                            context.getDrawable(
                                R.drawable.ic_world
                            )

                        "blinkingState" -> {}
                        "Sound_Volume" -> label.background = context.getDrawable(R.drawable.ic_volume)
                        "Radio_Tuner.Name", "Radio_Text" -> label.background =
                            context.getDrawable(R.drawable.ic_radio)

                        "totalDistance.distanceValue" -> label.background =
                            context.getDrawable(R.drawable.ic_odometer)

                        "vehicleIdenticationNumber.VIN" -> label.background =
                            context.getDrawable(R.drawable.ic_vin)

                        "tyreStates.stateRearRight", "tyrePressures.pressureRearRight", "tyreTemperatures.temperatureRearRight" -> {
                            label.text = context.getString(R.string.label_tyreRR)
                            label.background = context.getDrawable(R.drawable.ic_tyre)
                        }

                        "tyreStates.stateRearLeft", "tyrePressures.pressureRearLeft", "tyreTemperatures.temperatureRearLeft" -> {
                            label.text = context.getString(R.string.label_tyreRL)
                            label.background = context.getDrawable(R.drawable.ic_tyre)
                        }

                        "tyreStates.stateFrontRight", "tyrePressures.pressureFrontRight", "tyreTemperatures.temperatureFrontRight" -> {
                            label.text = context.getString(R.string.label_tyreFR)
                            label.background = context.getDrawable(R.drawable.ic_tyre)
                        }

                        "tyreStates.stateFrontLeft", "tyrePressures.pressureFrontLeft", "tyreTemperatures.temperatureFrontLeft" -> {
                            label.text = context.getString(R.string.label_tyreFL)
                            label.background = context.getDrawable(R.drawable.ic_tyre)
                        }

                        "tankLevelPrimary", "torque-fuellevel_0x2f" ->                 //label.setText("1");
                            label.background = context.getDrawable(R.drawable.ic_fuel)

                        "tankLevelSecondary" ->                 //label.setText("2");
                            label.background = context.getDrawable(R.drawable.ic_fuel)

                        "torque-engineload_0x04" -> {
                            label.text = context.getString(R.string.label_load)
                            icon = "empty"
                        }

                        "torque-timing_advance_0x0e" -> label.background =
                            context.getDrawable(R.drawable.ic_timing)

                        "torque-intake_air_temperature_0x0f" -> {
                            label.text = context.getString(R.string.label_iat)
                            icon = "empty"
                        }

                        "torque-mass_air_flow_0x10" -> {
                            label.text = context.getString(R.string.label_maf)
                            icon = "empty"
                        }

                        "torque-throttle_position_0x11" -> label.background =
                            context.getDrawable(R.drawable.ic_throttle)

                        "torque-turboboost_0xff1202" -> label.background =
                            context.getDrawable(R.drawable.ic_turbo)

                        "torque-AFR_0xff1249" -> {
                            label.text = context.getString(R.string.label_afr)
                            icon = "empty"
                        }

                        "torque-AFRc_0xff124d" -> {
                            label.text = context.getString(R.string.label_afrc)
                            icon = "empty"
                        }

                        "torque-fueltrimshortterm1_0x06" -> {
                            label.text = context.getString(R.string.label_ftst1)
                            icon = "empty"
                        }

                        "torque-fueltrimlongterm1_0x07" -> {
                            label.text = context.getString(R.string.label_ftlt1)
                            icon = "empty"
                        }

                        "torque-fueltrimshortterm2_0x08" -> {
                            label.text = context.getString(R.string.label_ftst2)
                            icon = "empty"
                        }

                        "torque-fueltrimlongterm2_0x09" -> {
                            label.text = context.getString(R.string.label_ftlt2)
                            icon = "empty"
                        }

                        "torque-exhaustgastempbank1sensor1_0x78" -> {
                            label.text = "1"
                            label.background = context.getDrawable(R.drawable.ic_fuelpressure)
                        }

                        "torque-exhaustgastempbank1sensor2_0xff1282" -> {
                            label.text = "2"
                            label.background = context.getDrawable(R.drawable.ic_fuelpressure)
                        }

                        "torque-exhaustgastempbank1sensor3_0xff1283" -> {
                            label.text = "3"
                            label.background = context.getDrawable(R.drawable.ic_fuelpressure)
                        }

                        "torque-exhaustgastempbank1sensor4_0xff1284" -> {
                            label.text = "4"
                            label.background = context.getDrawable(R.drawable.ic_exhaust)
                        }

                        "torque-fuelrailpressure_0x23", "torque-fuelpressure_0x0a" -> label.background =
                            context.getDrawable(
                                R.drawable.ic_fuelpressure
                            )

                        "torque-absolutethrottlepostion_0x47" -> label.background =
                            context.getDrawable(R.drawable.ic_throttle)

                        "torque-catalysttemperature_0x3c" -> label.background =
                            context.getDrawable(R.drawable.ic_catalyst)

                        "torque-chargeaircoolertemperature_0x77" -> label.background =
                            context.getDrawable(R.drawable.ic_cact)

                        "torque-commandedequivalenceratiolambda_0x44" -> label.text = "λ"
                        "torque-o2sensor1equivalenceratio_0x34" -> label.text = "O²"
                        "torque-phonebarometer_0xff1270" -> label.background =
                            context.getDrawable(R.drawable.ic_barometer)

                        "torque-engineloadabsolute_0x43" -> label.text = "Load"
                        "torque-phonebatterylevel_0xff129a" -> label.background =
                            context.getDrawable(R.drawable.ic_phone)

                        "torque-obdadaptervoltage_0xff1238" -> label.background =
                            context.getDrawable(R.drawable.ic_obd2)

                        "torque-intakemanifoldpressure_0x0b" -> label.background =
                            context.getDrawable(R.drawable.ic_manifold)

                        "torque-pressurecontrol_0x70" -> label.background =
                            context.getDrawable(R.drawable.ic_turbo)

                        "torque-relativethrottleposition_0x45" -> label.background =
                            context.getDrawable(R.drawable.ic_throttle)

                        "torque-voltagemodule_0x42" -> label.background =
                            context.getDrawable(R.drawable.ic_voltage)

                        else -> {
                            label.text = ""
                            value.text = ""
                            icon = "empty"
                        }
                    }
                    if (icon == "empty") {
                        label.setBackgroundResource(0)
                        val params = label.layoutParams as ConstraintLayout.LayoutParams
                        params.width = 40
                        label.layoutParams = params
                    }
                }
            }
        }
    }

    fun setupTypeface(typeface: Typeface) {
        valueElement?.typeface = typeface
        iconElement?.typeface = typeface
    }
}