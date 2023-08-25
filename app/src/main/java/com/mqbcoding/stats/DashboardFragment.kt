package com.mqbcoding.stats

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.apps.auto.sdk.StatusBarController

class DashboardFragment: CarFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val TAG = "DashboardFragment"
    private var rootView: View? = null
    private var dashboardId = 1
    private val torqueRefresher = TorqueRefresher()
    private val torqueService = TorqueService()

    private var mBtnNext: ImageButton? = null
    private var mBtnPrev: ImageButton? = null
    private var mTitleElement: TextView? = null
    private var mTitleElementLeft: TextView? = null
    private var mTitleElementRight: TextView? = null

    private var guages = arrayOfNulls<TorqueGauge>(3)
    private var displays = arrayOfNulls<TorqueDisplay>(4)

    private val pressureUnits: Boolean? = null
    private val temperatureUnits: Boolean? = null
    private var stagingDone: Boolean? = null
    private var raysOn: Boolean? = null
    private var maxOn: Boolean? = null
    private var maxMarksOn: Boolean? = null
    private var ticksOn: Boolean? = null
    private var ambientOn: Boolean? = null
    private var accurateOn: Boolean? = null
    private var proximityOn: Boolean? = null
    private var Dashboard2_On: Boolean? = null
    private var Dashboard3_On: Boolean? = null
    private var Dashboard4_On: Boolean? = null
    private var Dashboard5_On: Boolean? = null
    private var updateSpeed = 2000
    private var selectedFont: String? = null
    private var selectedTheme: String? = null
    private var selectedBackground: String? = null
    private var selectedPressureUnits = false
    private var DISPLAY_OFFSET = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        torqueService.startTorque(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView")
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        rootView = view

        mBtnNext = view.findViewById(R.id.imageButton2)
        mBtnPrev = view.findViewById(R.id.imageButton3)
        mTitleElementLeft = view.findViewById(R.id.textTitleElementLeft)
        mTitleElementRight = view.findViewById(R.id.textTitleElementRight)
        mTitleElement = view.findViewById(R.id.textTitleElement)

        guages[0] = childFragmentManager.findFragmentById(R.id.gaugeLeft) as TorqueGauge?
        guages[1] = childFragmentManager.findFragmentById(R.id.gaugeCenter) as TorqueGauge?
        guages[2] = childFragmentManager.findFragmentById(R.id.gaugeRight) as TorqueGauge?
        displays[0] = childFragmentManager.findFragmentById(R.id.display1) as TorqueDisplay?
        displays[1] = childFragmentManager.findFragmentById(R.id.display2) as TorqueDisplay?
        displays[2] = childFragmentManager.findFragmentById(R.id.display3) as TorqueDisplay?
        displays[3] = childFragmentManager.findFragmentById(R.id.display4) as TorqueDisplay?
        onSharedPreferenceChanged(getSharedPreferences(), "")
        return rootView
    }

    fun getSharedPreferences(): SharedPreferences {
        return requireContext().getSharedPreferences(
            "${requireContext().getPackageName()}_preferences",
            Context.MODE_PRIVATE
        )
    }

    override fun setupStatusBar(sc: StatusBarController) {
        sc.hideTitle()
    }

    override fun onDestroy() {
        super.onDestroy()
        torqueService.onDestroy(requireContext())
        torqueService.requestQuit(requireContext())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getSharedPreferences().registerOnSharedPreferenceChangeListener(this)
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        ambientOn = sharedPreferences.getBoolean(
            "ambientActive",
            false
        ) //true = use ambient colors, false = don't use.
        accurateOn = sharedPreferences.getBoolean(
            "accurateActive",
            false
        ) //true = be accurate. false = have 2000ms of animation time
        proximityOn = sharedPreferences.getBoolean(
            "proximityActive",
            false
        ) //true = be accurate. false = have 2000ms of animation time
        updateSpeed = if (accurateOn!!) {
            1
        } else {
            2000
        }

        val readedFont = sharedPreferences.getString("selectedFont", "segments")

        if (readedFont != selectedFont && readedFont != null) {
            selectedFont = readedFont
            val assetsMgr = context!!.assets
            var typeface = Typeface.createFromAsset(assetsMgr, "digital.ttf")
            when (selectedFont) {
                "segments" -> typeface = Typeface.createFromAsset(assetsMgr, "digital.ttf")
                "seat" -> typeface =
                    Typeface.createFromAsset(assetsMgr, "SEAT_MetaStyle_MonoDigit_Regular.ttf")

                "audi" -> typeface = Typeface.createFromAsset(assetsMgr, "AudiTypeDisplayHigh.ttf")
                "vw" -> typeface = Typeface.createFromAsset(assetsMgr, "VWTextCarUI-Regular.ttf")
                "vw2" -> typeface = Typeface.createFromAsset(assetsMgr, "VWThesis_MIB_Regular.ttf")
                "frutiger" -> typeface = Typeface.createFromAsset(assetsMgr, "Frutiger.otf")
                "vw3" -> typeface = Typeface.createFromAsset(assetsMgr, "VW_Digit_Reg.otf")
                "skoda" -> typeface = Typeface.createFromAsset(assetsMgr, "Skoda.ttf")
                "larabie" -> typeface = Typeface.createFromAsset(assetsMgr, "Larabie.ttf")
                "ford" -> typeface = Typeface.createFromAsset(assetsMgr, "UnitedSans.otf")
            }
            setupTypeface(typeface)
        }

        // Load this only on first run, then leave it alone
        if (stagingDone == null) {
            stagingDone = !sharedPreferences.getBoolean("stagingActive", true)
        }
        val readedBackground =
            sharedPreferences.getString("selectedBackground", "background_incar_black")
        if (readedBackground != selectedBackground) {
            setupBackground(readedBackground)
        }


        //determine what data the user wants to have on the 4 data views
        for (idx in 0..3) {
            val readedElementQuery = sharedPreferences.getString("selectedView${idx}_$dashboardId", "none") ?: "none"
            if (torqueRefresher.hasChanged(idx, readedElementQuery)) {
                torqueRefresher.populateQuery(idx + DISPLAY_OFFSET, readedElementQuery)
                setupElement(idx, readedElementQuery)
            }
        }
        //determine what data the user wants to have on the 3 clocks, but set defaults first
        //setup clocks, including the max/min clocks and highvis rays and icons:
        //usage: setupClocks(query value, what clock, what icon, which ray, which min clock, which max clock)
        //could probably be done MUCH more efficient but that's for the future ;)
        val positions = arrayOf("Left", "Center", "Right")
        for (pos in 0..2) {
            sharedPreferences.getString("selectedClock${positions[pos]}$dashboardId", "exlap-batteryVoltage")
                ?.let {
                    torqueRefresher.populateQuery(pos, it)
                    setupClocks(pos, it)
                }
        }


        //

        //show texts and backgrounds for max/min, according to the setting
        val readedMaxOn = sharedPreferences.getBoolean(
            "maxValuesActive",
            false
        ) //true = show max values, false = hide them
        if (maxOn == null || readedMaxOn != maxOn) {
            maxOn = readedMaxOn
            turnMinMaxTextViewsEnabled(maxOn!!)
        }
        val readedMaxMarksOn = sharedPreferences.getBoolean(
            "maxMarksActive",
            false
        ) //true = show max values as a mark on the clock, false = hide them
        if (maxMarksOn == null || readedMaxMarksOn != maxMarksOn) {
            maxMarksOn = readedMaxMarksOn
            turnMinMaxMarksEnabled(maxMarksOn!!)
        }

        val readedRaysOn = sharedPreferences.getBoolean(
            "highVisActive",
            false
        ) //true = show high vis rays, false = don't show them.
        val readedTheme = sharedPreferences.getString("selectedTheme", "")
        if (raysOn == null || readedRaysOn != raysOn || readedTheme != selectedTheme) {
            raysOn = readedRaysOn
            selectedTheme = readedTheme
            turnRaysEnabled(raysOn!!)
        }
        val readedTicksOn = sharedPreferences.getBoolean(
            "ticksActive",
            false
        ) // if true, it will display the value of each of the ticks
        if (ticksOn == null || readedTicksOn != ticksOn) {
            ticksOn = readedTicksOn
            turnTickEnabled(ticksOn!!)
        }
    }

    private fun setupBackground(newBackground: String?) {
        val resId = resources.getIdentifier(newBackground, "drawable", requireContext().packageName)
        if (resId != 0) {
            val wallpaperImage = ContextCompat.getDrawable(requireContext(), resId)
            rootView!!.background = wallpaperImage
        }
        selectedBackground = newBackground
    }

    fun setupTypeface(typeface: Typeface) {
        for (gauge in guages) {
            gauge?.setupTypeface(typeface)
        }
        for (display in displays) {
            display?.setupTypeface(typeface)
        }
        mTitleElement!!.typeface = typeface
        mTitleElementRight!!.typeface = typeface
        mTitleElementLeft!!.typeface = typeface
        Log.d(TAG, "font: $typeface")
    }

    fun setupElement(idx: Int, query: String) {
        displays[idx]!!.setupElement(query)
    }

    fun setupClocks(idx: Int, query: String) {
        guages[idx]!!.setupClock(query, 1, 100)
    }

    fun turnMinMaxTextViewsEnabled(enabled: Boolean) {
        Log.i(TAG, "Min max text view enabled: $enabled")
        for (gauge in guages) {
            gauge?.turnMinMaxTextViewsEnabled(enabled)
        }
    }

    fun turnMinMaxMarksEnabled(enabled: Boolean) {
        Log.i(TAG, "Min max marks enabled: $enabled")
        for (gauge in guages) {
            gauge?.turnMinMaxMarksEnabled(enabled)
        }
    }

    fun turnRaysEnabled(enabled: Boolean) {
        Log.i(TAG, "Rays enabled: $enabled")
        for (gauge in guages) {
            gauge?.turnRaysEnabled(enabled)
        }
    }

    fun turnTickEnabled(enabled: Boolean) {
        Log.i(TAG, "Tick enabled: $enabled")
        for (gauge in guages) {
            gauge?.turnTickEnabled(enabled)
        }
    }
}