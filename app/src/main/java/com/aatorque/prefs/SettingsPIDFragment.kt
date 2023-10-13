package com.aatorque.prefs

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.InputType
import androidx.lifecycle.lifecycleScope
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.aatorque.datastore.Display
import com.aatorque.datastore.MaxControl
import com.aatorque.datastore.Screen
import com.aatorque.stats.R
import com.aatorque.stats.TorqueServiceWrapper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.ceil
import kotlin.math.floor


class SettingsPIDFragment:  PreferenceFragmentCompat() {
    var prefCat: PreferenceCategory? = null
    var mBound: Boolean = false

    var isClock = true
    var screen = 0
    var index = 0

    lateinit var pidPref: ListPreference
    lateinit var showLabelPref: CheckBoxPreference
    lateinit var labelPref: EditTextPreference
    lateinit var imagePref: ImageListPreference
    lateinit var minValuePref: EditTextPreference
    lateinit var maxValuePref: EditTextPreference
    lateinit var unitPref: EditTextPreference
    lateinit var runcustomScriptPref: CheckBoxPreference
    lateinit var jsPref: FormulaPreference
    lateinit var wholeNumberPref: CheckBoxPreference
    lateinit var gaugeSettings: PreferenceCategory
    lateinit var ticksActivePref: CheckBoxPreference
    lateinit var maxValuesActivePref: ListPreference
    lateinit var maxMarksActivePref: ListPreference
    lateinit var highVisActivePref: CheckBoxPreference

    var torqueService: TorqueServiceWrapper? = null

    var torqueConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            torqueService = (service as TorqueServiceWrapper.LocalBinder).getService()
            torqueService!!.loadPidInformation(false) {
                pids ->
                requireActivity().runOnUiThread {
                    val valuesQuery = pids.map { "torque_${it.first}" }.toTypedArray()
                    val names = pids.map { it.second[0] }.toTypedArray()
                    pidPref.entryValues = arrayOf("") + valuesQuery
                    pidPref.entries = arrayOf(getString(R.string.element_none)) + names
                    prefCat!!.isEnabled = true
                    prefCat!!.summary = null
                }
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            torqueService = null
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
        minValuePref = findPreference("minValue")!!
        maxValuePref = findPreference("maxValue")!!
        unitPref = findPreference("unit")!!
        runcustomScriptPref = findPreference("runcustomScript")!!
        jsPref = findPreference("customScript")!!
        wholeNumberPref = findPreference("wholeNumbers")!!
        gaugeSettings = findPreference("gaugeSettings")!!
        ticksActivePref = findPreference("ticksActive")!!
        maxValuesActivePref = findPreference("maxValuesActive")!!
        maxMarksActivePref = findPreference("maxMarksActive")!!
        highVisActivePref = findPreference("highVisActive")!!

        pidPref.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        imagePref.setSummaryProvider {
            return@setSummaryProvider if (imagePref.value == "") {
                resources.getString(R.string.icon_null_desc)
            } else {
                ListPreference.SimpleSummaryProvider.getInstance().provideSummary(imagePref)
            }
        }
        imagePref.bgColor = R.color.accent
        labelPref.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        minValuePref.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        maxValuePref.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        unitPref.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        showLabelPref.setSummaryProvider {
            val ref = if (isClock) R.string.show_label_desc_clock else R.string.show_label_desc
            resources.getString(ref)
        }

        val editListen = EditTextPreference.OnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        }
        minValuePref.setOnBindEditTextListener(editListen)
        maxValuePref.setOnBindEditTextListener(editListen)
        maxValuesActivePref.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        maxMarksActivePref.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()

        pidPref.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == "") {
                enableItems(false)
            } else {
                val pidOnly = (newValue as String).substring("torque_".length)
                torqueService!!.pids!!.firstOrNull {
                    it.first == pidOnly
                }?.let {entryVal ->
                    var minVal = ceil(entryVal.second[3].toDouble()).toInt()
                    var maxVal = floor(entryVal.second[4].toDouble()).toInt()
                    if (minVal == 0 && maxVal == 0) {
                        minVal = 0
                        maxVal = 100
                    }
                    labelPref.text = entryVal.second[1]
                    maxValuePref.text = minVal.toString()
                    minValuePref.text = maxVal.toString()
                    unitPref.text = entryVal.second[2]
                }
                enableItems(true)
            }
            return@setOnPreferenceChangeListener true
        }

        mBound = TorqueServiceWrapper.runStartIntent(requireContext(), torqueConnection)
        showLabelPref.setOnPreferenceChangeListener { _, newValue ->
            if (!isClock) {
                labelPref.isEnabled = (newValue as Boolean)
                imagePref.isEnabled = !newValue
            }
            return@setOnPreferenceChangeListener true
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val data = requireContext().dataStore.data.first()
            val screen = data.getScreens(screen)
            val display = if (isClock) screen.getGauges(index) else screen.getDisplays(index)
            pidPref.value = display.pid
            showLabelPref.isChecked = display.showLabel
            labelPref.text = display.label
            imagePref.value = display.icon
            minValuePref.text = display.minValue.toString()
            maxValuePref.text = display.maxValue.toString()
            unitPref.text = display.unit
            runcustomScriptPref.isChecked = display.enableScript
            wholeNumberPref.isChecked = display.wholeNumbers
            ticksActivePref.isChecked = display.ticksActive
            maxValuesActivePref.value = display.maxValuesActive.number.toString()
            maxMarksActivePref.value = display.maxMarksActive.number.toString()
            highVisActivePref.isChecked = display.highVisActive
            jsPref.setValue(display.customScript)
            if (pidPref.value.startsWith("torque")) {
                enableItems(true)
            }
        }
    }

    fun enableItems(enabled: Boolean) {
        unitPref.isEnabled = enabled
        runcustomScriptPref.isEnabled = enabled
        showLabelPref.isEnabled = enabled
        if (isClock) {
            gaugeSettings.isEnabled = enabled
            imagePref.isEnabled = enabled
            labelPref.isEnabled = enabled
        } else {
            imagePref.isEnabled = if (enabled) !showLabelPref.isChecked else false
            labelPref.isEnabled = if (enabled) showLabelPref.isChecked else false
        }
    }

    override fun onPause() {
        super.onPause()
        if (pidPref.value != null && mBound) {
            saveState()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun saveState() {
        val minVal = minValuePref.text!!.toInt()
        val maxVal = maxValuePref.text!!.toInt()
        var display = Display.newBuilder().setPid(
            pidPref.value
        ).setShowLabel(
            showLabelPref.isChecked
        ).setLabel(
            labelPref.text
        ).setMinValue(
            minVal.coerceAtMost(maxVal)
        ).setMaxValue(
            maxVal.coerceAtLeast(minVal)
        ).setUnit(
            unitPref.text
        ).setEnableScript(
            runcustomScriptPref.isChecked
        ).setCustomScript(
            jsPref.getValue()
        ).setWholeNumbers(
            wholeNumberPref.isChecked
        ).setTicksActive(
            ticksActivePref.isChecked
        ).setMaxMarksActive(
            MaxControl.forNumber(maxMarksActivePref.value.toInt())
        ).setMaxValuesActive(
            MaxControl.forNumber(maxValuesActivePref.value.toInt())
        ).setHighVisActive(
            highVisActivePref.isChecked
        )
        if (imagePref.value != null) {
            display = display.setIcon(imagePref.value)
        }
        val context = requireContext()
        GlobalScope.launch(Dispatchers.IO) {
            context.dataStore.updateData {
                    currentSettings ->
                return@updateData currentSettings.toBuilder().let { set1 ->
                    var screenObj: Screen.Builder = try {
                        set1.getScreens(screen).toBuilder()
                    } catch (e: IndexOutOfBoundsException) {
                        Screen.newBuilder()
                    }
                    screenObj = screenObj.let screen@{
                        if (isClock) {
                            return@screen it.setGauges(index, display)
                        } else {
                            return@screen it.setDisplays(index, display)
                        }
                    }
                    return@let set1.setScreens(screen, screenObj)
                }.build()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBound) {
            try {
                requireContext().unbindService(torqueConnection)
            }catch(e: IllegalArgumentException) {
                Timber.e("Failed to unbind service", e)
            }
            mBound = false
        }
    }

}