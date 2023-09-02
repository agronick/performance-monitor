package com.mqbcoding.prefs

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.mqbcoding.datastore.UserPreference
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.InputStream
import java.io.OutputStream


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    /**
     * Some other code…
     */


    /**
     * For Proto Data Store
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    object UserPreferenceSerializer : Serializer<UserPreference> {
        override val defaultValue: UserPreference = UserPreference.getDefaultInstance()

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

    private val Context.recentLocationsDataStore: DataStore<UserPreference> by dataStore(
        fileName = "UserPreference.pb",
        serializer = UserPreferenceSerializer
    )

    @Provides
    @Reusable
    fun provideProtoDataStore(@ApplicationContext context: Context) =
        context.recentLocationsDataStore

}