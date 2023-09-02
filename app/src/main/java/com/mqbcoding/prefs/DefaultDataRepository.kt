package com.mqbcoding.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import com.mqbcoding.datastore.UserPreference
import javax.inject.Inject

class DefaultDataRepository @Inject constructor(
    private val context: Context,
    private val dataStore: DataStore<UserPreference>
) {



}