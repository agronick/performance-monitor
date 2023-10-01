package com.aatorque.prefs

import android.os.Bundle
import android.text.InputType
import timber.log.Timber
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
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
    lateinit var backgroundPref: ImageListPreference
    lateinit var themePref: ImageListPreference
    lateinit var fontPref: ImageListPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardsCat = findPreference("dashboardsCat")!!
        numScreensPref = findPreference("dashboardCount")!!
        backgroundPref = findPreference("selectedBackground")!!
        themePref = findPreference("selectedTheme")!!
        fontPref = findPreference("selectedFont")!!
        themePref.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        fontPref.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        backgroundPref.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        numScreensPref.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        numScreensPref.setOnPreferenceChangeListener {
                _, newValue ->
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
                            Timber.i("${newItms.size} added, ${intVal} specified")
                        }
                        return@updateData bldr.build()
                    }
                }
                return@setOnPreferenceChangeListener true
            }
            return@setOnPreferenceChangeListener false
        }

        numScreensPref.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
        }

        val baseTitle = requireContext().getString(
            R.string.pref_dataelementsettings_1
        )
        lifecycleScope.launch {
            requireContext().dataStore.data.distinctUntilChangedBy{
                it.screensCount
            }.collect { userPreference ->
                numScreensPref.text = userPreference.screensCount.toString()
                dashboardsCat.removeAll()
                userPreference.screensList.forEachIndexed {
                        i, screen ->
                    dashboardsCat.addPreference(Preference(requireContext()).also {
                        it.title = baseTitle.replace("1", (i + 1).toString())
                        it.key = "dashboard_$i"
                        it.fragment = SettingsDashboard::class.java.canonicalName
                        it.summary = screen.title
                    })
                }
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }
}