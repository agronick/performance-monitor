package com.mqbcoding.stats

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.preference.ListPreference
import android.preference.Preference.OnPreferenceChangeListener
import android.util.AttributeSet
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.children
import com.mqbcoding.prefs.UserPreferenceSerializer
import com.mqbcoding.prefs.dataStore
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.Collections

class SettingsFragment : PreferenceFragmentCompat() {
    lateinit var numScreensPref: EditTextPreference
    lateinit var dashboardsCat: PreferenceCategory

    @Throws(IOException::class)
    private fun findLogs(): List<File> {
        val logDir = CarStatsLogger.getLogsDir()
        val files: MutableList<File> = ArrayList()
        for (f in logDir.listFiles()) {
            if (f.name.endsWith(".log.gz")) {
                files.add(f)
            }
        }
        Collections.sort(files)
        return files
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardsCat = findPreference("dashboardsCat")!!
        numScreensPref = findPreference("dashboardCount")!!
        numScreensPref.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()

        lifecycleScope.launch {
            requireContext().dataStore.data.collect { userPreference ->
                dashboardsCat.removeAll()
                numScreensPref.text = userPreference.screensCount.toString()
                userPreference.screensList.forEachIndexed {
                        i, screen ->
                    dashboardsCat.addPreference(Preference(requireContext()).also {
                        it.title = requireContext().getString(
                            R.string.pref_dataelementsettings_1
                        ).replace("1", (i + 1).toString())
                        it.key = "dashboard_$i"
                        it.fragment = "com.mqbcoding.stats.SettingsDashboard"
                        it.summary = screen.title
                    })
                }
            }
        }

        numScreensPref.setOnPreferenceChangeListener {
                preference, newValue ->
            val intVal = (newValue as String).toInt()
            if (intVal in 1..10) {
                lifecycleScope.launch {
                    requireContext().dataStore.updateData { currentSettings ->
                        var bldr = currentSettings.toBuilder()
                        if (bldr.screensCount > intVal) {
                            val keeping = bldr.screensList.subList(0, intVal)
                            bldr = bldr.clearScreens().addAllScreens(keeping)
                        } else {
                            for (i in bldr.screensCount - 1..intVal) {
                                bldr = bldr.addScreens(
                                    UserPreferenceSerializer.defaultValue.getScreens(0)
                                )
                            }
                        }
                        return@updateData bldr.build()
                    }
                }
                return@setOnPreferenceChangeListener true
            }
            return@setOnPreferenceChangeListener false
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }

    companion object {
        private const val TAG = "PreferenceFragment"
        private val sBindPreferenceSummaryToValueListener =
            OnPreferenceChangeListener { preference, value ->
                Log.d(TAG, "Pereference change: " + preference.key)
                val stringValue = value?.toString() ?: ""
                if (preference is ListPreference) {
                    val listPreference = preference
                    val index = listPreference.findIndexOfValue(stringValue)
                    preference.setSummary(
                        if (index >= 0) listPreference.entries[index] else null
                    )
                }
                if (preference is TemperaturePreference) {
                    return@OnPreferenceChangeListener true
                } else {
                    preference.summary = stringValue
                }
                true
            }

    }
}