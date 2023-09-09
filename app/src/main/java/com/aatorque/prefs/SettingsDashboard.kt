package com.aatorque.prefs

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.aatorque.stats.R
import com.aatorque.stats.TorqueServiceWrapper
import kotlinx.coroutines.launch

class SettingsDashboard: PreferenceFragmentCompat() {

    lateinit var performanceTitle: EditTextPreference
    lateinit var mainCat: PreferenceCategory

    val clockText = arrayOf(
        R.string.pref_leftclock,
        R.string.pref_centerclock,
        R.string.pref_rightclock,
    )
    val clockIcon = arrayOf(
        R.drawable.ic_settings_clockl,
        R.drawable.ic_settings_clockc,
        R.drawable.ic_settings_clockr,
    )
    val displayIcon = arrayOf(
        R.drawable.ic_settings_view1,
        R.drawable.ic_settings_view2,
        R.drawable.ic_settings_view3,
        R.drawable.ic_settings_view4,
    )
    val displayText = arrayOf(
        R.string.pref_view1,
        R.string.pref_view2,
        R.string.pref_view3,
        R.string.pref_view4,
    )

    var torqueConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val torqueService = (service as TorqueServiceWrapper.LocalBinder).getService()
            torqueService!!.loadPidInformation(false) {
                    pids, detailsQuery ->
                val valuesQuery = pids.map { "torque_${it}" }.toTypedArray()
                requireActivity().runOnUiThread {
                    mainCat.title = resources.getText(R.string.pref_dataelementsettings_1).replace("1".toRegex(), (dashboardIndex() + 1).toString())
                    val dbIndex = dashboardIndex()
                    lifecycleScope.launch {
                        requireContext().dataStore.data.collect { userPreference ->
                            val screen = userPreference.getScreens(dbIndex)
                            performanceTitle.text = screen.title
                            mainCat.removeAll()
                            mainCat.addPreference(performanceTitle)
                            val sources = arrayOf(screen.gaugesList, screen.displaysList)
                            val texts = arrayOf(clockText, displayText)
                            val icons = arrayOf(clockIcon, displayIcon)
                            arrayOf("clock", "display").forEachIndexed { i, type ->
                                sources[i].forEachIndexed { j, screen ->
                                    mainCat.addPreference(
                                        Preference(requireContext()).also {
                                            it.key = "${type}_${dbIndex}_${j}"
                                            if (valuesQuery.contains(screen.pid)) {
                                                it.summary = detailsQuery.get(valuesQuery.indexOf(screen.pid))[0]
                                            }
                                            it.title = requireContext().getString(texts[i][j])
                                            it.icon = AppCompatResources.getDrawable(requireContext(), icons[i][j])
                                            it.fragment = "com.aatorque.prefs.SettingsPIDFragment"
                                        }
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            TODO("Not yet implemented")
        }
    }

    fun dashboardIndex(): Int {
        return requireArguments().getCharSequence("prefix")?.split("_")!!.last().toInt()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TorqueServiceWrapper.runStartIntent(requireContext(), torqueConnection)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.display_setting)
        mainCat = findPreference("displayCat")!!
        performanceTitle = findPreference("performanceTitle")!!
        performanceTitle.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        performanceTitle.setOnPreferenceChangeListener {
                preference, newValue ->
            lifecycleScope.launch {
                requireContext().dataStore.updateData {
                    val screen = it.getScreens(dashboardIndex()).toBuilder().setTitle(newValue as String)
                    return@updateData it.toBuilder().setScreens(dashboardIndex(), screen).build()
                }
            }
            return@setOnPreferenceChangeListener true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unbindService(torqueConnection)
    }

}