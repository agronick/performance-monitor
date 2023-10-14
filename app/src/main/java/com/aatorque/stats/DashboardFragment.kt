package com.aatorque.stats

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.InputDeviceCompat
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import com.aatorque.prefs.dataStore
import com.aatorque.stats.databinding.FragmentDashboardBinding
import com.google.android.apps.auto.sdk.StatusBarController
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.abs


open class DashboardFragment : CarFragment() {
    lateinit var rootView: View
    lateinit var mLayoutDashboard: ConstraintLayout

    val torqueRefresher = TorqueRefresher()
    private val torqueService = TorqueService()

    private lateinit var mBtnNext: ImageButton
    private lateinit var mBtnPrev: ImageButton
    private lateinit var mTitleElement: TextView
    private lateinit var mWrapper: RelativeLayout
    lateinit var mConStatus: TextView

    var guages = arrayOfNulls<TorqueGauge>(3)
    var displays = arrayOfNulls<TorqueDisplay>(4)
    var gaugeViews = arrayOfNulls<FragmentContainerView>(3)

    private var screensAnimating = false
    private var mStarted = false
    lateinit var binding: FragmentDashboardBinding

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
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        this.rootView = binding.root

        mLayoutDashboard = rootView.findViewById(R.id.layoutDashboard)
        mBtnNext = rootView.findViewById(R.id.imageButton2)
        mBtnPrev = rootView.findViewById(R.id.imageButton3)
        mBtnNext.setOnClickListener { setScreen(1) }
        mBtnPrev.setOnClickListener  { setScreen(-1) }
        mTitleElement = rootView.findViewById(R.id.textTitle)
        mWrapper = rootView.findViewById(R.id.include_wrap)
        mConStatus = rootView.findViewById(R.id.con_status)
        gaugeViews[0] = rootView.findViewById(R.id.gaugeLeft)
        gaugeViews[1] = rootView.findViewById(R.id.gaugeCenter)
        gaugeViews[2] = rootView.findViewById(R.id.gaugeRight)

        guages[0] = childFragmentManager.findFragmentById(R.id.gaugeLeft)!! as TorqueGauge
        guages[1] = childFragmentManager.findFragmentById(R.id.gaugeCenter)!! as TorqueGauge
        guages[2] = childFragmentManager.findFragmentById(R.id.gaugeRight)!! as TorqueGauge
        displays[0] = childFragmentManager.findFragmentById(R.id.display1)!! as TorqueDisplay
        displays[1] = childFragmentManager.findFragmentById(R.id.display2)!! as TorqueDisplay
        displays[2] = childFragmentManager.findFragmentById(R.id.display3)!! as TorqueDisplay
        displays[3] = childFragmentManager.findFragmentById(R.id.display4)!! as TorqueDisplay
        displays[2]!!.isBottomDisplay = true
        displays[3]!!.isBottomDisplay = true
        return this.rootView
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            requireContext().dataStore.data.map {
                it.screensList[abs(it.currentScreen) % it.screensCount]
            }.distinctUntilChanged().collect { screens ->
                binding.title = screens.title
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
            binding.status = when (it) {
                ConnectStatus.CONNECTING_TORQUE -> R.string.status_connecting_torque
                ConnectStatus.CONNECTING_ECU -> R.string.status_connecting_to_ecu
                ConnectStatus.SETUP_GAUGE -> R.string.status_setup_gauges
                ConnectStatus.CONNECTED -> null
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
        binding.largeCenter = largeCenter
    }

    private fun setupBackground(newBackground: String?) {
        val resId = resources.getIdentifier(newBackground, "drawable", requireContext().packageName)
        if (resId != 0) {
            binding.background = resId
        }
    }

    fun configureRotaryInput(enabled: Boolean) {
        binding.showBtns = !enabled
        if (enabled) {
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
        }
    }

    fun setupTypeface(selectedFont: String) {
        Timber.d("font: $selectedFont")
        val font = when (selectedFont) {
            "segments" -> R.font.digital
            "seat" -> R.font.seat_metastyle_monodigit_regular
            "audi" -> R.font.auditypedisplayhigh
            "vw" -> R.font.vwtextcarui_regular
            "vw2" -> R.font.vwthesis_mib_regular
            "frutiger" -> R.font.frutiger
            "vw3" -> R.font.vw_digit_reg
            "skoda" -> R.font.skoda
            "larabie" -> R.font.larabie
            "ford" -> R.font.unitedsans
            else -> R.font.digital
        }
        val typeface = ResourcesCompat.getFont(requireContext(), font)!!
            for (gauge in guages) {
            gauge?.setupTypeface(typeface)
        }
        for (display in displays) {
            display?.setupTypeface(typeface)
        }
        binding.font = typeface
    }
}
