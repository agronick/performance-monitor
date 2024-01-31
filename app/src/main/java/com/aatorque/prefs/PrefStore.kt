package com.aatorque.prefs

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.aatorque.datastore.Display
import com.aatorque.datastore.Screen
import com.aatorque.datastore.UserPreference
import com.google.protobuf.TextFormat
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream


const val DEFAULT_SETTINGS = """
screens {
  gauges {
    pid: "torque_0c,0"
    showLabel: true
    label: "RPM"
    icon: "ic_cylinder"
    maxValue: 10000
    unit: "rpm"
    wholeNumbers: true
    ticksActive: true
    chartColor: -12734743
    disabled: false
  }
  gauges {
    pid: "torque_0d,0"
    showLabel: true
    label: "Speed"
    icon: "ic_barometer"
    maxValue: 160
    unit: "km/h"
    highVisActive: true
    ticksActive: true
    chartColor: -5314243
    disabled: false
  }
  gauges {
    pid: "torque_11,0"
    showLabel: true
    label: "Throttle"
    icon: "ic_throttle"
    maxValue: 100
    unit: "%"
    ticksActive: true
    chartColor: -1476547
    disabled: false
  }
  displays {}
  displays {}
  displays {}
  displays {}
}
selectedTheme: "Electro Vehicle"
selectedFont: "ev"
selectedBackground: "background_incar_ev"
centerGaugeLarge: true
"""

object UserPreferenceSerializer : Serializer<UserPreference> {
    val defaultGauge = Display.newBuilder()
        .setShowLabel(true)
    val defaultDisplay = Display.newBuilder()
    val defaultScreen = Screen.newBuilder()
        .addGauges(defaultGauge)
        .addGauges(defaultGauge)
        .addGauges(defaultGauge)
        .addDisplays(defaultDisplay)
        .addDisplays(defaultDisplay)
        .addDisplays(defaultDisplay)
        .addDisplays(defaultDisplay)

    override var defaultValue: UserPreference

    init {
        defaultValue = try {
            TextFormat.parse(
                DEFAULT_SETTINGS,
                UserPreference::class.java
            )
        } catch (e: Exception) {
            Timber.e("Failed to load defaults", e)
            UserPreference.newBuilder().addScreens(defaultScreen).build()
        }
    }

    override suspend fun readFrom(input: InputStream): UserPreference {
        try {
            return UserPreference.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        } catch (e: java.io.IOException) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun writeTo(t: UserPreference, output: OutputStream) = t.writeTo(output)

}


val Context.dataStore: DataStore<UserPreference> by dataStore(
    fileName = "user_prefs.pb",
    serializer = UserPreferenceSerializer
)

