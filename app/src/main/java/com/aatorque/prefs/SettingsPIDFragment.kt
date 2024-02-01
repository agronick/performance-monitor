package com.aatorque.prefs

import android.content.ComponentName
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.text.InputType
import androidx.lifecycle.lifecycleScope
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.aatorque.datastore.Display
import com.aatorque.datastore.MaxControl
import com.aatorque.datastore.Screen
import com.aatorque.stats.R
import com.aatorque.stats.TorqueServiceWrapper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.ceil
import kotlin.math.floor


class SettingsPIDFragment:  PreferenceFragmentCompat() {
    lateinit var prefCat: PreferenceCategory
    var mBound: Boolean = false

    var isClock = true
    var screen = 0
    var index = 0

    lateinit var display: Display
    lateinit var enabledPref: SwitchPreferenceCompat
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
    lateinit var colorPref: ColorPreference

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
                    prefCat.isEnabled = true
                    prefCat.summary = null
                }
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            torqueService = null
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pid_setting)
        prefCat = findPreference("pidCategory")!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = requireArguments()
        val prefix = args.getString("prefix")
        isClock = args.getBoolean("isClock")
        screen = args.getInt("screen")
        index = args.getInt("index")
        preferenceManager.sharedPreferencesName = null

        enabledPref = findPreference("enabled")!!
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
        colorPref = findPreference("chartColor")!!
        findPreference<Preference>("indicatorColor")!!.extras.putCharSequence("prefix", prefix)

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

        lifecycleScope.launch {
            requireContext().dataStore.data.map {
                val screen = it.getScreens(screen)
                return@map if (isClock) screen.getGauges(index) else screen.getDisplays(index)
            }.collect{
                display = it
                enabledPref.isChecked = !display.disabled
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
                colorPref.colorValue = display.chartColor.let {
                    if (!isClock) {
                        Color.WHITE
                    } else if (it == 0) {
                        resources.obtainTypedArray(R.array.chartColors).run {
                            val color = getColor(index, Color.WHITE)
                            recycle()
                            color
                        }
                    } else it
                }
                jsPref.setValue(display.customScript)
                if (pidPref.value.startsWith("torque")) {
                    enableItems(true)
                }
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
        fun coerce(value: String?, default: Int): Int {
            return try {
                value?.toInt() ?: default
            } catch (e: NumberFormatException) {
                default
            }
        }

        val minVal = coerce(minValuePref.text, 0)
        val maxVal = coerce(maxValuePref.text, 100)
        var display = display.toBuilder().setDisabled(
            !enabledPref.isChecked
        ).setPid(
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
        ).setChartColor(
            colorPref.colorValue
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