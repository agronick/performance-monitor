package com.mqbcoding.stats

import android.app.AlertDialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.os.IBinder
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
import androidx.datastore.core.DataStore
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceFragmentCompat
import com.austingreco.imagelistpreference.ImageListPreference
import com.github.martoreto.aauto.vex.CarStatsClient
import com.mqbcoding.datastore.UserPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.Collections
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val connection = TorqueServiceWrapper.getConnection(true)

    @Inject
    lateinit var dataStore: DataStore<UserPreference>
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

    var torqueConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val torqueService = (service as TorqueServiceWrapper.LocalBinder).getService()
            torqueService!!.loadPidInformation(false) {
                    pids, detailsQuery ->
                requireActivity().runOnUiThread {
                    val valuesQuery = pids.map { "torque_${it}" }.toTypedArray()

                    lifecycleScope.launch {
                        dataStore.data.collect { userPreference ->
                            userPreference.screensList.forEachIndexed {
                                i, screen ->
                                screen.gaugesList.forEachIndexed {
                                    j, screen ->
                                    if (valuesQuery.contains(screen.pid)) {
                                        findPreference<
                                                androidx.preference.Preference
                                                >("clock_${i}_${j}")?.summary =
                                            detailsQuery.get(valuesQuery.indexOf(screen.pid))[0]
                                    }
                                }
                                screen.displaysList.forEachIndexed {
                                        j, screen ->
                                    if (valuesQuery.contains(screen.pid)) {
                                        findPreference<
                                                androidx.preference.Preference
                                                >("display_${i}_${j}")?.summary =
                                            detailsQuery.get(valuesQuery.indexOf(screen.pid))[0]
                                    }
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Intent(requireContext(), TorqueServiceWrapper::class.java).also { intent ->
            requireActivity().bindService(intent, torqueConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unbindService(torqueConnection)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
        lifecycleScope.launch {
            dataStore.data.collect { userPreference ->
                for (screen in userPreference.screensList) {
                    for (gauge in screen.gaugesList)  {
                        gauge.pid
                    }
                }
            }
        }
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