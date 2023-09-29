package com.aatorque.stats

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.InputDeviceCompat
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStateAtLeast
import com.google.android.apps.auto.sdk.StatusBarController
import com.aatorque.prefs.dataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.math.abs


class DashboardFragment : CarFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var rootView: View? = null
    private val torqueRefresher = TorqueRefresher()
    private val torqueService = TorqueService()

    private var mBtnNext: ImageButton? = null
    private var mBtnPrev: ImageButton? = null
    private var mTitleElement: TextView? = null
    private lateinit var mWrapper: ConstraintLayout
    lateinit var mConStatus: TextView

    private var guages = arrayOfNulls<TorqueGauge>(3)
    private var displays = arrayOfNulls<TorqueDisplay>(4)
    private var gaugeViews = arrayOfNulls<FragmentContainerView>(3)

    private var selectedFont: String? = null
    private var selectedBackground: String? = null
    private var screensAnimating = false
    private var mStarted = false

    companion object {
        const val TAG = "DashboardFragment"
        const val DISPLAY_OFFSET = 3
    }

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
        mBtnNext!!.setOnClickListener { setScreen(1) }
        mBtnPrev!!.setOnClickListener  { setScreen(-1) }
        mTitleElement = view.findViewById(R.id.textTitle)
        mWrapper = view.findViewById(R.id.include_wrap)
        mConStatus = view.findViewById(R.id.con_status)
        gaugeViews[0] = view.findViewById(R.id.gaugeLeft)
        gaugeViews[1] = view.findViewById(R.id.gaugeCenter)
        gaugeViews[2] = view.findViewById(R.id.gaugeRight)

        guages[0] = childFragmentManager.findFragmentById(R.id.gaugeLeft)!! as TorqueGauge
        guages[1] = childFragmentManager.findFragmentById(R.id.gaugeCenter)!! as TorqueGauge
        guages[2] = childFragmentManager.findFragmentById(R.id.gaugeRight)!! as TorqueGauge
        displays[0] = childFragmentManager.findFragmentById(R.id.display1)!! as TorqueDisplay
        displays[1] = childFragmentManager.findFragmentById(R.id.display2)!! as TorqueDisplay
        displays[2] = childFragmentManager.findFragmentById(R.id.display3)!! as TorqueDisplay
        displays[3] = childFragmentManager.findFragmentById(R.id.display4)!! as TorqueDisplay
        displays[2]!!.isBottomDisplay = true
        displays[3]!!.isBottomDisplay = true
        return rootView
    }

    override fun onStart() {
        super.onStart()
        val initialRun = !mStarted
        mStarted = true
        lifecycleScope.launch {
            requireContext().dataStore.data.map {
                it.screensList[abs(it.currentScreen) % it.screensCount]
            }.collect { screens ->
                if (mStarted) {
                    mTitleElement?.text = screens.title
                }
                screens.gaugesList.forEachIndexed { index, display ->
                    if (initialRun || torqueRefresher.hasChanged(index, display)) {
                        val clock = torqueRefresher.populateQuery(index, display)
                        if (mStarted) {
                            guages[index]?.setupClock(clock)
                        }
                    }
                }
                screens.displaysList.forEachIndexed { index, display ->
                    if (initialRun || torqueRefresher.hasChanged(index + DISPLAY_OFFSET, display)) {
                        val td = torqueRefresher.populateQuery(index + DISPLAY_OFFSET, display)
                        if (mStarted) {
                            displays[index]?.setupElement(td)
                        }
                    }
                }
                torqueRefresher.makeExecutors(torqueService)
            }
        }
        onSharedPreferenceChanged(getSharedPreferences(), "")
    }

    fun setScreen(direction: Int) {
        if (screensAnimating || torqueRefresher.lastConnectStatus == false) return
        screensAnimating = true
        val duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        mTitleElement!!.animate().alpha(0f).duration = duration
        mWrapper.animate()!!.translationX((rootView!!.width * -direction).toFloat()).setDuration(
            duration
        ).alpha(0f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                lifecycleScope.launch {
                    requireContext().dataStore.updateData {
                            currentSettings ->
                        currentSettings.toBuilder().setCurrentScreen(
                            (currentSettings.screensCount +
                                    currentSettings.currentScreen +
                                    direction
                                    ) % currentSettings.screensCount
                        ).build()
                    }
                    mWrapper.translationX = (rootView!!.width * direction).toFloat()
                    mWrapper.alpha = 1f
                    mWrapper.animate().setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            screensAnimating = false
                        }
                    }).translationX(0f).duration = duration
                    mTitleElement!!.animate().alpha(1f).duration = duration
                }
            }
        })
    }

    fun getSharedPreferences(): SharedPreferences {
        return requireContext().getSharedPreferences(
            "${requireContext().packageName}_preferences",
            Context.MODE_PRIVATE
        )
    }

    override fun setupStatusBar(sc: StatusBarController) {
        sc.hideTitle()
    }

    override fun onStop() {
        super.onStop()
        mStarted = false
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        torqueRefresher.makeExecutors(torqueService)
        torqueRefresher.watchConnection(torqueService) {
            if (it == null || it == false) {
                mConStatus.visibility = View.VISIBLE
                mConStatus.text = resources.getText(
                    if (it == null) R.string.status_connecting_torque else R.string.status_connecting_to_ecu
                )
            } else {
                mConStatus.visibility = View.INVISIBLE
            }
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
        torqueRefresher.stopExecutors()
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
        if (context == null) return

        configureRotaryInput(sharedPreferences.getBoolean("rotaryInput", false))

        val readedFont = sharedPreferences.getString("selectedFont", "segments")

        if (readedFont != selectedFont && readedFont != null) {
            selectedFont = readedFont
            val assetsMgr = requireContext().assets
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

        val readedBackground =
            sharedPreferences.getString("selectedBackground", "background_incar_black")
        if (readedBackground != selectedBackground) {
            setupBackground(readedBackground)
        }
        updateScale(sharedPreferences.getBoolean("centerGaugeLarge", false))
    }

    private fun updateScale(largeCenter: Boolean) {
        val scaleFactor = if (largeCenter) resources.getFraction(R.dimen.scale_gauge, 1, 1) else 1f
        gaugeViews[0]!!.scaleX = scaleFactor
        gaugeViews[0]!!.scaleY = scaleFactor
        gaugeViews[2]!!.scaleX = scaleFactor
        gaugeViews[2]!!.scaleY = scaleFactor
    }

    private fun setupBackground(newBackground: String?) {
        val resId = resources.getIdentifier(newBackground, "drawable", requireContext().packageName)
        if (resId != 0) {
            val wallpaperImage = ContextCompat.getDrawable(requireContext(), resId)
            rootView!!.background = wallpaperImage
        }
        selectedBackground = newBackground
    }

    private fun configureRotaryInput(enabled: Boolean) {
        if (enabled) {
            mBtnPrev?.visibility = View.INVISIBLE
            mBtnNext?.visibility = View.INVISIBLE
            rootView!!.setOnGenericMotionListener { _, ev ->
                if (ev.action == MotionEvent.ACTION_SCROLL &&
                    ev.isFromSource(InputDeviceCompat.SOURCE_MOUSE)
                ) {
                    val delta = ev.getAxisValue(MotionEvent.AXIS_VSCROLL)
                    setScreen(if (delta < 0) 1 else -1)
                    true
                } else {
                    false
                }
            }
        } else {
            mBtnPrev?.visibility = View.VISIBLE
            mBtnNext?.visibility = View.VISIBLE
            rootView?.setOnGenericMotionListener(null)
        }
    }

    fun setupTypeface(typeface: Typeface) {
        for (gauge in guages) {
            gauge?.setupTypeface(typeface)
        }
        for (display in displays) {
            display?.setupTypeface(typeface)
        }
        mTitleElement!!.typeface = typeface
        Log.d(TAG, "font: $typeface")
    }


}