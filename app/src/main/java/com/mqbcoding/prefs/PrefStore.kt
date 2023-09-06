package com.mqbcoding.prefs

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.mqbcoding.datastore.Display
import com.mqbcoding.datastore.Screen
import com.mqbcoding.datastore.UserPreference
import java.io.InputStream
import java.io.OutputStream

object UserPreferenceSerializer : Serializer<UserPreference> {
    val defaultGauge = Display.newBuilder()
    val defaultScreen = Screen.newBuilder()
        .addGauges(defaultGauge)
        .addGauges(defaultGauge)
        .addGauges(defaultGauge)
        .addDisplays(defaultGauge)
        .addDisplays(defaultGauge)
        .addDisplays(defaultGauge)
        .addDisplays(defaultGauge)

    override val defaultValue: UserPreference = UserPreference.newBuilder().addScreens(
        defaultScreen
    ).build()

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