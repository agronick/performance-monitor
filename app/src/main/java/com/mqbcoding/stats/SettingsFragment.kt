package com.mqbcoding.stats

import android.app.AlertDialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceManager
import android.preference.SwitchPreference
import android.util.ArrayMap
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import com.austingreco.imagelistpreference.ImageListPreference
import com.github.martoreto.aauto.vex.CarStatsClient
import java.io.File
import java.io.IOException
import java.util.Collections

class SettingsFragment : PreferenceFragmentCompat() {
    private var torqueService = TorqueService {
        val pids = it.listAllPIDsIncludingDetectedPIDs()
        val details = it.getPIDInformation(pids).map { it -> it.substringBefore(",") }
        val valuesQuery = pids.map { it -> "torque_${it}" }
        for (pos in arrayOf(
            "Left",
            "Center",
            "Right",
        )) {
            val prefs = findPreference<androidx.preference.ListPreference>("selectedClock${pos}1")
            prefs!!.entryValues = valuesQuery.toTypedArray()
            prefs.entries = details.toTypedArray()
        }
    }

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


    override fun onResume() {
        super.onResume()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
        torqueService.startTorque(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        torqueService.onDestroy(requireContext())
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