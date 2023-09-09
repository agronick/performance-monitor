package com.aatorque.prefs

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.aatorque.stats.R
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import java.util.Collections

class SettingsFragment : PreferenceFragmentCompat() {
    lateinit var numScreensPref: EditTextPreference
    lateinit var dashboardsCat: PreferenceCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardsCat = findPreference("dashboardsCat")!!
        numScreensPref = findPreference("dashboardCount")!!
        numScreensPref.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
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
                        } else if (currentSettings.screensCount < intVal) {
                            val newItms = Collections.nCopies(
                                intVal - currentSettings.screensCount,
                                UserPreferenceSerializer.defaultScreen.build()
                            )
                            bldr = bldr.addAllScreens(newItms.toMutableList())
                            Log.d(TAG, "${newItms.size} added, ${intVal} specified")
                        }
                        return@updateData bldr.build()
                    }
                }
                return@setOnPreferenceChangeListener true
            }
            return@setOnPreferenceChangeListener false
        }

        lifecycleScope.launch {
            requireContext().dataStore.data.distinctUntilChangedBy{
                it.screensCount
            }.collect { userPreference ->
                dashboardsCat.removeAll()
                userPreference.screensList.forEachIndexed {
                        i, screen ->
                    dashboardsCat.addPreference(Preference(requireContext()).also {
                        it.title = requireContext().getString(
                            R.string.pref_dataelementsettings_1
                        ).replace("1", (i + 1).toString())
                        it.key = "dashboard_$i"
                        it.fragment = "com.aatorque.prefs.SettingsDashboard"
                        it.summary = screen.title
                    })
                }
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }

    companion object {
        private const val TAG = "PreferenceFragment"
    }
}