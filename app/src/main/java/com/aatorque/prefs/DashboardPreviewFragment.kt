package com.aatorque.prefs

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.aatorque.stats.DashboardFragment
import com.aatorque.stats.R


class DashboardPreviewFragment: DashboardFragment() {
    override val layout = R.layout.preview_dashboard
    val handler = Handler(Looper.getMainLooper())
    var firstRun = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val preferences = getSharedPreferences()
        val readedTheme: String? = preferences.getString("selectedTheme", "VW GTI")
        inflater.context.setTheme(mapTheme(readedTheme))
        (requireActivity() as SettingsActivity).supportActionBar!!.hide()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firstRun = savedInstanceState?.getBoolean("firstRun") ?: firstRun
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        forceRotate(true)
    }

    override fun onStart() {
        super.onStart()
        configureRotaryInput(false)
    }

    fun forceRotate(isOn: Boolean) {
        requireActivity().requestedOrientation = if(isOn) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as SettingsActivity).supportActionBar!!.hide()
        forceRotate(true)
        val window = requireActivity().window
        WindowCompat.setDecorFitsSystemWindows(window, false);
        WindowInsetsControllerCompat(window, window.decorView).hide(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.statusBars()
        )
        WindowCompat.getInsetsController(window, window.decorView).systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        if (firstRun) {
            // Force needles to redraw
            handler.postDelayed({
                for (index in 0..<DISPLAY_OFFSET) {
                    torqueRefresher.data[index]?.let { guages[index]?.setupClock(it) }
                }
            }, 0)
            firstRun = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("firstRun", firstRun)
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as SettingsActivity).supportActionBar!!.show()
        forceRotate(false)
    }

}

fun mapTheme(theme: String?): Int {
    return when (theme) {
        "VW GTI" -> R.style.AppTheme_VolkswagenGTI
        "VW R/GTE" -> R.style.AppTheme_VolkswagenGTE
        "VW" -> R.style.AppTheme_Volkswagen
        "VW MIB2" -> R.style.AppTheme_VolkswagenMIB2
        "VW AID" -> R.style.AppTheme_VolkswagenAID
        "Seat Cupra" -> R.style.AppTheme_SeatCupra
        "Cupra Division" -> R.style.AppTheme_Cupra
        "Audi TT" -> R.style.AppTheme_AudiTT
        "Seat" -> R.style.AppTheme_Seat
        "Skoda" -> R.style.AppTheme_Skoda
        "Skoda ONE" -> R.style.AppTheme_SkodaOneApp
        "Skoda vRS" -> R.style.AppTheme_SkodavRS
        "Skoda Virtual Cockpit" -> R.style.AppTheme_SkodaVC
        "Audi" -> R.style.AppTheme_Audi
        "Audi Virtual Cockpit" -> R.style.AppTheme_AudiVC
        "Clubsport" -> R.style.AppTheme_Clubsport
        "Minimalistic" -> R.style.AppTheme_Minimalistic
        "Test" -> R.style.AppTheme_Testing
        "Dark" -> R.style.AppTheme_Dark
        "Mustang GT" -> R.style.AppTheme_Ford
        "BMW" -> R.style.AppTheme_BMW
        else -> R.style.AppTheme_VolkswagenMIB2
    }
}