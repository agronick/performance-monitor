package com.aatorque.prefs

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.aatorque.stats.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class DashboardPreviewFragment: Fragment() {
    val handler = Handler(Looper.getMainLooper())
    companion object {
        val hideBars = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.navigationBars()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard_preview, container, false)
        val data = runBlocking {
            requireContext().dataStore.data.first()
        }
        inflater.context.setTheme(mapTheme(data.selectedTheme))
        forceRotate(true)
        return view
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
        forceRotate(true)

        val window = requireActivity().window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).hide(hideBars)
        WindowCompat.getInsetsController(window, window.decorView).systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onPause() {
        super.onPause()
        val window = requireActivity().window
        forceRotate(false)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).show(hideBars)
    }

}

@StyleRes
fun mapTheme(theme: String?):  Int {
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