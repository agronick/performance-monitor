package com.aatorque.stats

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Typeface
import android.os.Bundle
import timber.log.Timber
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
import androidx.lifecycle.lifecycleScope
import com.google.android.apps.auto.sdk.StatusBarController
import com.aatorque.prefs.dataStore
import com.aatorque.prefs.mapTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.skip
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.abs


open class DashboardFragment : CarFragment() {
    lateinit var rootView: View
    lateinit var mLayoutDashboard: ConstraintLayout

    val torqueRefresher = TorqueRefresher()
    private val torqueService = TorqueService()

    private lateinit var mBtnNext: ImageButton
    private lateinit var mBtnPrev: ImageButton
    private lateinit var mTitleElement: TextView
    private lateinit var mWrapper: ConstraintLayout
    lateinit var mConStatus: TextView

    var guages = arrayOfNulls<TorqueGauge>(3)
    var displays = arrayOfNulls<TorqueDisplay>(4)
    var gaugeViews = arrayOfNulls<FragmentContainerView>(3)

    private var screensAnimating = false
    private var mStarted = false
    protected open val layout = R.layout.fragment_dashboard

    companion object {
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
        Timber.i("onCreateView")
        val view = inflater.inflate(layout, container, false)

        rootView = view

        mLayoutDashboard = view.findViewById(R.id.layoutDashboard)
        mBtnNext = view.findViewById(R.id.imageButton2)
        mBtnPrev = view.findViewById(R.id.imageButton3)
        mBtnNext.setOnClickListener { setScreen(1) }
        mBtnPrev.setOnClickListener  { setScreen(-1) }
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
        lifecycleScope.launch {
            requireContext().dataStore.data.map {
                it.screensList[abs(it.currentScreen) % it.screensCount]
            }.distinctUntilChanged().collect { screens ->
                mTitleElement.text = screens.title
                screens.gaugesList.forEachIndexed { index, display ->
                    if (torqueRefresher.hasChanged(index, display)) {
                        val clock = torqueRefresher.populateQuery(index, display)
                        guages[index]?.setupClock(clock)
                    }
                }
                screens.displaysList.forEachIndexed { index, display ->
                    if (torqueRefresher.hasChanged(index + DISPLAY_OFFSET, display)) {
                        val td = torqueRefresher.populateQuery(index + DISPLAY_OFFSET, display)
                        displays[index]?.setupElement(td)
                    }
                }
                torqueRefresher.makeExecutors(torqueService)
            }
        }
        lifecycleScope.launch {
            requireContext().dataStore.data.map {
                it.selectedFont
            }.distinctUntilChanged().collect(
                this@DashboardFragment::setupTypeface
            )
        }
        lifecycleScope.launch {
            requireContext().dataStore.data.map {
                it.selectedBackground
            }.distinctUntilChanged().collect(
                this@DashboardFragment::setupBackground
            )
        }
        lifecycleScope.launch {
            requireContext().dataStore.data.map {
                it.rotaryInput
            }.distinctUntilChanged().collect(
                this@DashboardFragment::configureRotaryInput
            )
        }
        lifecycleScope.launch {
            requireContext().dataStore.data.map {
                it.centerGaugeLarge
            }.distinctUntilChanged().collect(
                this@DashboardFragment::updateScale
            )
        }
    }

    fun setScreen(direction: Int) {
        if (screensAnimating) return
        screensAnimating = true
        val duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        mTitleElement.animate().alpha(0f).duration = duration
        mWrapper.animate()!!.translationX((rootView.width * -direction).toFloat()).setDuration(
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
                    mWrapper.translationX = (rootView.width * direction).toFloat()
                    mWrapper.alpha = 1f
                    mWrapper.animate().setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            screensAnimating = false
                        }
                    }).translationX(0f).duration = duration
                    mTitleElement.animate().alpha(1f).duration = duration
                }
            }
        })
    }

    override fun setupStatusBar(sc: StatusBarController) {
        sc.hideTitle()
    }

    override fun onStop() {
        super.onStop()
        mStarted = false
    }

    override fun onResume() {
        Timber.d("onResume")
        super.onResume()
        torqueRefresher.makeExecutors(torqueService)
        torqueRefresher.watchConnection(torqueService) {
            if (it != ConnectStatus.CONNECTED) {
                mConStatus.visibility = View.VISIBLE
                mConStatus.text = resources.getText(
                    when(it) {
                        ConnectStatus.CONNECTING_TORQUE -> R.string.status_connecting_torque
                        ConnectStatus.CONNECTING_ECU -> R.string.status_connecting_to_ecu
                        ConnectStatus.SETUP_GAUGE -> R.string.status_setup_gauges
                        else -> throw Exception("Unknown status")
                    }
                )
            } else {
                mConStatus.visibility = View.INVISIBLE
            }
        }
    }

    override fun onPause() {
        Timber.d("onPause")
        super.onPause()
        torqueRefresher.stopExecutors()
    }

    override fun onDestroy() {
        super.onDestroy()
        torqueService.onDestroy(requireContext())
        torqueService.requestQuit(requireContext())
    }

    private fun updateScale(largeCenter: Boolean) {
        val scaleFactor = if (largeCenter) resources.getFraction(R.fraction.scale_gauge, 1, 1) else 1f
        gaugeViews[0]!!.scaleX = scaleFactor
        gaugeViews[0]!!.post {
            val preWidth = gaugeViews[0]!!.width * scaleFactor
            val postWidth = gaugeViews[0]!!.width
            val halfDiff = ((preWidth - postWidth) * 0.25f)
            displays[0]!!.rootView?.translationX = halfDiff
            displays[2]!!.rootView?.translationX = halfDiff
        }
        gaugeViews[0]!!.scaleY = scaleFactor
        gaugeViews[2]!!.scaleX = scaleFactor
        gaugeViews[2]!!.scaleY = scaleFactor
        gaugeViews[2]!!.post {
            val preWidth = gaugeViews[0]!!.width * scaleFactor
            val postWidth = gaugeViews[0]!!.width
            val halfDiff = ((preWidth - postWidth) * 0.25f)
            displays[1]!!.rootView?.translationX = -halfDiff
            displays[3]!!.rootView?.translationX = -halfDiff
        }
    }

    private fun setupBackground(newBackground: String?) {
        val resId = resources.getIdentifier(newBackground, "drawable", requireContext().packageName)
        if (resId != 0) {
            val wallpaperImage = ContextCompat.getDrawable(requireContext(), resId)
            mLayoutDashboard.background = wallpaperImage
        }
    }

    fun configureRotaryInput(enabled: Boolean) {
        if (enabled) {
            mBtnPrev.visibility = View.INVISIBLE
            mBtnNext.visibility = View.INVISIBLE
            rootView.setOnGenericMotionListener { _, ev ->
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
            mBtnPrev.visibility = View.VISIBLE
            mBtnNext.visibility = View.VISIBLE
            rootView.setOnGenericMotionListener(null)
        }
    }

    fun setupTypeface(selectedFont: String) {
        val assetsMgr = requireContext().assets
        Timber.d("font: $selectedFont")
        var typeface = Typeface.createFromAsset(assetsMgr, when (selectedFont) {
            "segments" -> "digital.ttf"
            "seat" -> "SEAT_MetaStyle_MonoDigit_Regular.ttf"
            "audi" -> "AudiTypeDisplayHigh.ttf"
            "vw" -> "VWTextCarUI-Regular.ttf"
            "vw2" -> "VWThesis_MIB_Regular.ttf"
            "frutiger" -> "Frutiger.otf"
            "vw3" -> "VW_Digit_Reg.otf"
            "skoda" -> "Skoda.ttf"
            "larabie" -> "Larabie.ttf"
            "ford" -> "UnitedSans.otf"
            else -> "digital.ttf"
        })
        for (gauge in guages) {
            gauge?.setupTypeface(typeface)
        }
        for (display in displays) {
            display?.setupTypeface(typeface)
        }
        mTitleElement.typeface = typeface
    }
}