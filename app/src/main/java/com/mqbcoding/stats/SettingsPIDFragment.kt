package com.mqbcoding.stats

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.mqbcoding.datastore.Display
import com.mqbcoding.datastore.Screen
import com.mqbcoding.datastore.UserPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsPIDFragment:  PreferenceFragmentCompat() {
    val TAG = "SettingsPIDFragment"
    var prefCat: PreferenceCategory? = null
    var service: TorqueService? = null
    var mBound: Boolean = false

    var isClock = true
    var screen = 0
    var index = 0

    lateinit var pidPref: ListPreference
    lateinit var showLabelPref: CheckBoxPreference
    lateinit var labelPref: EditTextPreference
    lateinit var imagePref: ImageListPreference

    lateinit var prefix: String
    @Inject
    lateinit var dataStore: DataStore<UserPreference>

    var torqueConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            (service as TorqueServiceWrapper.LocalBinder).getService().loadPidInformation(false) {
                pids, detailsQuery ->
                requireActivity().runOnUiThread {
                    val valuesQuery = pids.map { "torque_${it}" }.toTypedArray()
                    pidPref.entryValues = valuesQuery
                    pidPref.entries = detailsQuery.map { it[0] }.toTypedArray()
                    prefCat!!.isEnabled = true
                    prefCat!!.summary = null
                }
            }
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pid_setting)
        prefCat = findPreference("pidCategory")
        assert(prefCat != null)
        prefCat?.title = requireArguments().getCharSequence("title")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val parts = requireArguments().getCharSequence("prefix")?.split("_")
        assert(parts!!.size == 3)
        isClock = parts[0] == "clock"
        screen = parts[1].toInt()
        index = parts[2].toInt()
        preferenceManager.sharedPreferencesName = null

        pidPref = findPreference("pidList")!!
        showLabelPref = findPreference("showLabel")!!
        labelPref = findPreference("label")!!
        imagePref = findPreference("image")!!
        dataStore.data.map {
            val screen = it.getScreens(screen)
            val display = if (isClock) screen.getGauges(index) else screen.getDisplays(index)
            pidPref?.value = display.pid
            showLabelPref?.isChecked = display.showLabel
            labelPref?.text = display.label
            imagePref?.value = display.icon
        }
        Intent(requireContext(), TorqueServiceWrapper::class.java).also { intent ->
            if (!requireContext().bindService(intent, torqueConnection, Context.BIND_AUTO_CREATE)) {
                Log.e(TAG, "Failed to bind internal service")
            } else {
                Log.d(TAG, "Started intent for service wrapper")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val display = Display.newBuilder().setPid(
            pidPref.value
        ).setShowLabel(
            showLabelPref.isChecked
        ).setLabel(
            labelPref.text
        ).setIcon(
            imagePref.value
        )
        GlobalScope.launch(Dispatchers.IO) {
            dataStore.updateData {
                currentSettings ->
                return@updateData currentSettings.toBuilder().let {
                    val screenObj = it.getScreens(screen).toBuilder().let {
                        if (isClock) {
                            return@let it.setGauges(index, display)
                        } else {
                            return@let it.setDisplays(index, display)
                        }
                    }
                    return@let it.setScreens(screen, screenObj)
                }.build()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unbindService(torqueConnection)
    }

}