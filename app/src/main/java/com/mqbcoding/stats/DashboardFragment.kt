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
import androidx.fragment.app.Fragment
import com.google.android.apps.auto.sdk.StatusBarController
import org.prowl.torque.remote.ITorqueService

class DashboardFragment: FontFragmentBase() {
    private val TAG = "DashboardFragment"
    private var rootView: View? = null
    private var torqueService: ITorqueService? = null
    private var dashboardId = 0

    private var mBtnNext: ImageButton? = null
    private var mBtnPrev: ImageButton? = null
    private var mTitleElement: TextView? = null
    private var mTitleElementLeft: TextView? = null
    private var mTitleElementRight: TextView? = null

    private var guages = arrayOfNulls<Fragment>(3)
    private var displays = arrayOfNulls<Fragment>(4)

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
    private var selectedTheme: String? = null
    private var selectedBackground: String? = null
    private var selectedFont: String? = null
    private var selectedPressureUnits = false


    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key -> onPreferencesChangeHandler() }
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

        val fragmentManager = activity!!.supportFragmentManager
        guages[0] =  fragmentManager.findFragmentById(R.id.gaugeLeft)
        guages[1] = fragmentManager.findFragmentById(R.id.gaugeCenter)
        guages[2] = fragmentManager.findFragmentById(R.id.gaugeRight)
        displays[1] = fragmentManager.findFragmentById(R.id.display1)
        displays[2] = fragmentManager.findFragmentById(R.id.display2)
        displays[3] = fragmentManager.findFragmentById(R.id.display3)
        displays[4] = fragmentManager.findFragmentById(R.id.display4)
        return rootView
    }

    override fun setupStatusBar(sc: StatusBarController) {
        sc.hideTitle()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        startTorque()
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTorque()
    }

    private fun startTorque() {
        val intent = Intent()
        intent.setClassName("org.prowl.torque", "org.prowl.torque.remote.TorqueService")
        val torqueBind = context!!.bindService(intent, torqueConnection, Activity.BIND_AUTO_CREATE)
        Log.d(
            TAG,
            if (torqueBind) "Connected to torque service!" else "Unable to connect to Torque plugin service"
        )
    }

    private fun stopTorque() {
        context!!.sendBroadcast(Intent("org.prowl.torque.REQUEST_TORQUE_QUIT"))
        Log.d(TAG, "Torque stop")
    }

    private val torqueConnection: ServiceConnection = object : ServiceConnection {
        /**
         * What to do when we get connected to Torque.
         *
         * @param arg0
         * @param service
         */
        override fun onServiceConnected(arg0: ComponentName, service: IBinder) {
            torqueService = ITorqueService.Stub.asInterface(service)
        }

        /**
         * What to do when we get disconnected from Torque.
         *
         * @param name
         */
        override fun onServiceDisconnected(name: ComponentName) {
            torqueService = null
        }
    }

    override fun onPreferencesChangeHandler() {
        super.onPreferencesChangeHandler()
        val sharedPreferences = activity.getSharedPreferences(
            "shared_preferences",
            Context.MODE_PRIVATE,
        )
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
        for (idx in 1..4) {
            val readedElement1Query = sharedPreferences.getString("selectedView${idx}_$dashboardId", "none")
            if (readedElement1Query != mElement1Query) {
                mElement1Query = readedElement1Query
                setupElement(mElement1Query, mValueElement1, mIconElement1)
            }
        }
        //determine what data the user wants to have on the 3 clocks, but set defaults first
        //setup clocks, including the max/min clocks and highvis rays and icons:
        //usage: setupClocks(query value, what clock, what icon, which ray, which min clock, which max clock)
        //could probably be done MUCH more efficient but that's for the future ;)
        val readedClockLQuery =
            sharedPreferences.getString("selectedClockLeft$dashboardId", "exlap-batteryVoltage")
        if (readedClockLQuery != mClockLQuery) {
            mClockLQuery = readedClockLQuery
            setupClocks(mClockLQuery, mClockLeft, mIconClockL, mRayLeft, mClockMaxLeft)
            turnTickEnabled(ticksOn!!) // Due to bug in SpeedView, we need to re-enable ticks
        }
        val readedClockCQuery =
            sharedPreferences.getString("selectedClockCenter$dashboardId", "exlap-oilTemperature")
        if (readedClockCQuery != mClockCQuery) {
            mClockCQuery = readedClockCQuery
            setupClocks(mClockCQuery, mClockCenter, mIconClockC, mRayCenter, mClockMaxCenter)
            turnTickEnabled(ticksOn!!) // Due to bug in SpeedView, we need to re-enable ticks
        }
        val readedClockRQuery =
            sharedPreferences.getString("selectedClockRight$dashboardId", "exlap-engineSpeed")
        if (readedClockRQuery != mClockRQuery) {
            mClockRQuery = readedClockRQuery
            setupClocks(mClockRQuery, mClockRight, mIconClockR, mRayRight, mClockMaxRight)
            turnTickEnabled(ticksOn!!) // Due to bug in SpeedView, we need to re-enable ticks
        }
        //debug logging of each of the chosen elements
        Log.d(TAG, "element 1 selected:$mElement1Query")
        Log.d(TAG, "element 2 selected:$mElement2Query")
        Log.d(TAG, "element 3 selected:$mElement3Query")
        Log.d(TAG, "element 4 selected:$mElement4Query")
        Log.d(TAG, "clock l selected:$mClockLQuery")
        Log.d(TAG, "clock c selected:$mClockCQuery")
        Log.d(TAG, "clock r selected:$mClockRQuery")

        //determine what data the user wants to have on the 4 data views
        mLabelClockL = getLabelClock(mClockLQuery)
        mLabelClockC = getLabelClock(mClockCQuery)
        mLabelClockR = getLabelClock(mClockRQuery)
        val readedPressureUnits =
            sharedPreferences.getBoolean("selectPressureUnit", true) //true = bar, false = psi
        if (readedPressureUnits != selectedPressureUnits) {
            selectedPressureUnits = readedPressureUnits
            pressureFactor = if (selectedPressureUnits) 1f else 14.503774f
            pressureUnit = if (selectedPressureUnits) "bar" else "psi"
            pressureMin = if (selectedPressureUnits) -3 else -30
            pressureMax = if (selectedPressureUnits) 3 else 30
        }
        val readedTempUnit = sharedPreferences.getBoolean(
            "selectTemperatureUnit",
            true
        ) //true = celcius, false = fahrenheit
        celsiusTempUnit = readedTempUnit
        temperatureUnit = getString(if (celsiusTempUnit) R.string.unit_c else R.string.unit_f)
        val readedPowerUnits =
            sharedPreferences.getBoolean("selectPowerUnit", true) //true = kw, false = ps
        if (TorqueData.powerUnits == null || readedPowerUnits != TorqueData.powerUnits) {
            TorqueData.powerUnits = readedPowerUnits
            powerFactor = if (TorqueData.powerUnits!!) 1f else 1.35962f
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
    }

    private fun setupBackground(newBackground: String?) {
        val resId = resources.getIdentifier(newBackground, "drawable", context!!.packageName)
        if (resId != 0) {
            val wallpaperImage = ContextCompat.getDrawable(context!!, resId)
            rootView!!.background = wallpaperImage
        }
        selectedBackground = newBackground
    }

    override fun setupTypeface(typeface: Typeface) {
        mTitleElement!!.typeface = typeface
        mTitleElementRight!!.typeface = typeface
        mTitleElementLeft!!.typeface = typeface
        Log.d(TAG, "font: $typeface")
        this.selectedFont = selectedFont
    }
}