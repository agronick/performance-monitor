package com.mqbcoding.stats

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat


class SettingsPIDFragment:  PreferenceFragmentCompat() {
    var prefCat: PreferenceCategory? = null


    private var torqueService = TorqueService().addConnectCallback{
        val pids = it.listAllPIDs()
        val detailsQuery = it.getPIDInformation(pids).map { it.substringBefore(",") }.toTypedArray()
        val valuesQuery = pids.map { "torque_${it}" }.toTypedArray()
        val prefs = findPreference<androidx.preference.ListPreference>("pidList")
        prefs!!.entryValues = valuesQuery
        prefs.entries = detailsQuery
        prefCat!!.isEnabled = true
        prefCat!!.summary = null
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pid_setting)
        prefCat = findPreference<PreferenceCategory>("pidCategory")
        assert(prefCat != null)
        prefCat!!.title = arguments?.getCharSequence("title")
        if (!torqueService.startTorque(requireContext())) {
            Toast.makeText(context,
                "Torque does not appear to be available",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        torqueService.requestQuit(requireContext())
    }

}