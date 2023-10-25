@file:Suppress("MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASS")
package com.aatorque.stats

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.aatorque.prefs.dataStore
import com.aatorque.prefs.mapTheme
import com.aatorque.stats.ListMenuAdapter.MenuCallbacks
import com.google.android.apps.auto.sdk.CarActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainCarActivity : CarActivity() {
    private var mCurrentFragmentTag: String? = null
    private val mMenuCallbacks: MenuCallbacks = object : MenuCallbacks {
        override fun onMenuItemClicked(name: String) {
            when (name) {
                MENU_DASHBOARD -> switchToFragment(FRAGMENT_CAR)
                MENU_STOPWATCH -> switchToFragment(FRAGMENT_STOPWATCH)
                MENU_CREDITS -> switchToFragment(FRAGMENT_CREDITS)
            }
        }

        override fun onEnter() {}
        override fun onExit() {
            updateStatusBarTitle()
        }
    }
    private var inBg = false
    private var awaitingTheme: String? = null
    private var lastTheme: String? = null

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            val intent = Intent("KEY_DOWN").apply {
                putExtra("KEY_CODE", keyCode)
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
        return super.onKeyDown(keyCode, event)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        val data = runBlocking {
            dataStore.data.first()
        }
        GlobalScope.launch {
            dataStore.data.map {
                it.selectedTheme
            }.distinctUntilChanged().drop(1).collect(
                this@MainCarActivity::recreateWithTheme
            )
        }
        setLocalTheme(data.selectedTheme)
        setContentView(R.layout.activity_car_main)
        /*
        CarUiController carUiController = getCarUiController();
        //force night mode
        carUiController.getStatusBarController().setDayNightStyle(DayNightStyle.FORCE_NIGHT);
*/
        val fragmentManager = supportFragmentManager

        //set fragments:
        val carfragment: CarFragment = DashboardFragment()
        fragmentManager.beginTransaction()
            .add(R.id.fragment_container, carfragment, FRAGMENT_CAR)
            .detach(carfragment)
            .commitNow()
        var initialFragmentTag: String? = FRAGMENT_CAR
        if (bundle != null && bundle.containsKey(CURRENT_FRAGMENT_KEY)) {
            initialFragmentTag = bundle.getString(CURRENT_FRAGMENT_KEY)
        }
        switchToFragment(initialFragmentTag)
        /* todo: maybe restore this
        MenuController menuController = getCarUiController().getMenuController();
        if (!preferences.getBoolean("rotaryInput", false)) {
            menuController.showMenuButton();
        } */
        val statusBarController = carUiController.statusBarController
        carfragment.setupStatusBar(statusBarController)
        setIgnoreConfigChanges(0xFFFF)
    }


    override fun onSaveInstanceState(bundle: Bundle) {
        bundle.putString(CURRENT_FRAGMENT_KEY, mCurrentFragmentTag)
        inBg = true
        super.onSaveInstanceState(bundle)
    }

    override fun onStart() {
        super.onStart()
        switchToFragment(mCurrentFragmentTag)
    }

    fun recreateWithTheme(selectedTheme: String) {
        if (inBg) {
            awaitingTheme = selectedTheme
            return
        }
        if (!setLocalTheme(selectedTheme)) return

        val manager = supportFragmentManager
        val currentFragment =
            if (mCurrentFragmentTag == null) null else manager.findFragmentByTag(
                mCurrentFragmentTag
            )
        if (currentFragment != null) {
            val trans = manager.beginTransaction().detach(currentFragment)
            if (mCurrentFragmentTag == MENU_DASHBOARD) {
                trans.remove(currentFragment)
                    .add(R.id.fragment_container, DashboardFragment(), FRAGMENT_CAR)
            } else {
                trans.detach(currentFragment).attach(currentFragment)
            }
            trans.commit()
        }
    }

    private fun setLocalTheme(theme: String?): Boolean {
        if (lastTheme != theme) {
            lastTheme = theme
            setTheme(mapTheme(theme))
            return true
        }
        return false
    }

    private fun switchToFragment(tag: String?) {
        if (tag == mCurrentFragmentTag) {
            return
        }
        val manager = supportFragmentManager
        val currentFragment =
            if (mCurrentFragmentTag == null) null else manager.findFragmentByTag(mCurrentFragmentTag)
        val newFragment = manager.findFragmentByTag(tag)
        val transaction = supportFragmentManager.beginTransaction()
        if (currentFragment != null) {
            transaction.detach(currentFragment)
        }
        if (newFragment != null) {
            transaction.attach(newFragment)
            transaction.commitAllowingStateLoss()
            mCurrentFragmentTag = tag
        }
    }

    private fun updateStatusBarTitle() {
        val fragment = supportFragmentManager.findFragmentByTag(mCurrentFragmentTag) as CarFragment?
        if (fragment != null) carUiController.statusBarController.setTitle(fragment.title)
    }

    override fun onResume() {
        super.onResume()
        inBg = false
        awaitingTheme?.let(this::recreateWithTheme)
        awaitingTheme = null
    }

    companion object {
        //menu stuff//
        const val MENU_DASHBOARD = "dashboard"
        const val MENU_CREDITS = "credits"
        const val MENU_STOPWATCH = "stopwatch"
        private const val FRAGMENT_CAR = "dashboard"
        private const val FRAGMENT_CREDITS = "credits"
        private const val FRAGMENT_STOPWATCH = "stopwatch"
        private const val CURRENT_FRAGMENT_KEY = "app_current_fragment"
    }
}