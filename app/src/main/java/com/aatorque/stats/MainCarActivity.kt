package com.aatorque.stats

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.aatorque.stats.ListMenuAdapter.MenuCallbacks
import com.google.android.apps.auto.sdk.CarActivity
import com.google.android.apps.auto.sdk.MenuItem

class MainCarActivity : CarActivity() {
    private var mCurrentFragmentTag: String? = null
    private val connectivityOn: Boolean? = null
    private val batteryOn: Boolean? = null
    private val clockOn: Boolean? = null
    private val micOn: Boolean? = null
    private var preferences: SharedPreferences? = null
    private var selectedBackground: String? = null
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

    //end menu stuff//
    private val mFragmentLifecycleCallbacks: FragmentManager.FragmentLifecycleCallbacks =
        object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                updateStatusBarTitle()
            }
        }
    private val mHandler = Handler()
    private val preferenceChangeListener =
        OnSharedPreferenceChangeListener { sharedPreferences, key -> preferenceChangeHandler() }
    private var selectedTheme: String? = null
    override fun onResume() {
        preferences!!.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onPause() {
        preferences!!.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    private fun preferenceChangeHandler() {
        // Do we really need this looks like old code?
        val readedBackground = preferences!!.getString("selectedBackground", "Black")
        if (readedBackground != selectedBackground) {
            selectedBackground = readedBackground
            val resId = resources.getIdentifier(selectedBackground, "drawable", this.packageName)
            if (resId != 0) {
                val wallpaperImage = AppCompatResources.getDrawable(applicationContext, resId)
                val container = findViewById(R.id.fragment_container)
                container.setBackgroundResource(R.drawable.background_incar_black)
                container.background = wallpaperImage
            }
        }
        val readedTheme = preferences!!.getString("selectedTheme", "VW GTI")
        if (readedTheme != selectedTheme) {
            selectedTheme = readedTheme
            setLocalTheme(selectedTheme)
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
            c().decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            setIgnoreConfigChanges(0xFFFF)
        }
        carUiController.menuController.setRootMenuAdapter(createMenu())
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setLocalTheme("VW GTI")
        setContentView(R.layout.activity_car_main)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferenceChangeHandler()
        /*
        CarUiController carUiController = getCarUiController();
        //force night mode
        carUiController.getStatusBarController().setDayNightStyle(DayNightStyle.FORCE_NIGHT);
*/
        val fragmentManager = supportFragmentManager

        //set fragments:
        val carfragment: CarFragment = DashboardFragment()
        val stopwatchfragment = StopwatchFragment()
        val creditsfragment = CreditsFragment()
        fragmentManager.beginTransaction()
            .add(R.id.fragment_container, carfragment, FRAGMENT_CAR)
            .detach(carfragment)
            .add(R.id.fragment_container, stopwatchfragment, FRAGMENT_STOPWATCH)
            .detach(stopwatchfragment)
            .add(R.id.fragment_container, creditsfragment, FRAGMENT_CREDITS)
            .detach(creditsfragment)
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
    }

    private fun createMenu(): ListMenuAdapter {
        val mainMenu = ListMenuAdapter()
        mainMenu.setCallbacks(mMenuCallbacks)

        //set menu
        mainMenu.addMenuItem(
            MENU_DASHBOARD, MenuItem.Builder()
                .setTitle(getString(R.string.activity_main_title))
                .setType(MenuItem.Type.ITEM)
                .build()
        )

        /*
        mainMenu.addMenuItem(MENU_READINGS, new MenuItem.Builder()
                .setTitle(getString(R.string.activity_readings_title))
                .setType(MenuItem.Type.ITEM)
                .build());
         */mainMenu.addMenuItem(
            MENU_STOPWATCH, MenuItem.Builder()
                .setTitle(getString(R.string.activity_stopwatch_title))
                .setType(MenuItem.Type.ITEM)
                .build()
        )
        mainMenu.addMenuItem(
            MENU_CREDITS, MenuItem.Builder()
                .setTitle(getString(R.string.activity_credits_title))
                .setType(MenuItem.Type.ITEM)
                .build()
        )


// 1 submenu item
        /*   ListMenuAdapter otherMenu = new ListMenuAdapter();
        otherMenu.setCallbacks(mMenuCallbacks);
        otherMenu.addMenuItem(MENU_DEMO, new MenuItem.Builder()
                .setTitle("Demo")
                .setType(MenuItem.Type.ITEM)
                .build());*/

        //   mainMenu.addSubmenu(MENU_OTHER, otherMenu);
        return mainMenu
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        bundle.putString(CURRENT_FRAGMENT_KEY, mCurrentFragmentTag)
        super.onSaveInstanceState(bundle)
    }

    override fun onStart() {
        super.onStart()
        switchToFragment(mCurrentFragmentTag)
    }

    private fun setLocalTheme(theme: String?) {
        when (theme) {
            "VW GTI" -> setTheme(R.style.AppTheme_VolkswagenGTI)
            "VW R/GTE" -> setTheme(R.style.AppTheme_VolkswagenGTE)
            "VW" -> setTheme(R.style.AppTheme_Volkswagen)
            "VW MIB2" -> setTheme(R.style.AppTheme_VolkswagenMIB2)
            "VW AID" -> setTheme(R.style.AppTheme_VolkswagenAID)
            "Seat Cupra" -> setTheme(R.style.AppTheme_SeatCupra)
            "Cupra Division" -> setTheme(R.style.AppTheme_Cupra)
            "Audi TT" -> setTheme(R.style.AppTheme_AudiTT)
            "Seat" -> setTheme(R.style.AppTheme_Seat)
            "Skoda" -> setTheme(R.style.AppTheme_Skoda)
            "Skoda ONE" -> setTheme(R.style.AppTheme_SkodaOneApp)
            "Skoda vRS" -> setTheme(R.style.AppTheme_SkodavRS)
            "Skoda Virtual Cockpit" -> setTheme(R.style.AppTheme_SkodaVC)
            "Audi" -> setTheme(R.style.AppTheme_Audi)
            "Audi Virtual Cockpit" -> setTheme(R.style.AppTheme_AudiVC)
            "Clubsport" -> setTheme(R.style.AppTheme_Clubsport)
            "Minimalistic" -> setTheme(R.style.AppTheme_Minimalistic)
            "Test" -> setTheme(R.style.AppTheme_Testing)
            "Dark" -> setTheme(R.style.AppTheme_Dark)
            "Mustang GT" -> setTheme(R.style.AppTheme_Ford)
            "BMW" -> setTheme(R.style.AppTheme_BMW)
            else ->                 // set default theme:
                setTheme(R.style.AppTheme_VolkswagenMIB2)
        }
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

    companion object {
        private const val TAG = "MainCarActivity"

        //menu stuff//
        const val MENU_DASHBOARD = "dashboard"

        //  static final String MENU_CARINFO = "carinfo";
        const val MENU_READINGS = "readings"
        const val MENU_CREDITS = "credits"
        const val MENU_STOPWATCH = "stopwatch"

        // static final String MENU_DEBUG_LOG = "log";
        // static final String MENU_DEBUG_TEST_NOTIFICATION = "test_notification";
        private const val FRAGMENT_CAR = "dashboard"
        private const val FRAGMENT_READINGS = "readings"
        private const val FRAGMENT_CREDITS = "credits"
        private const val FRAGMENT_STOPWATCH = "stopwatch"
        private const val CURRENT_FRAGMENT_KEY = "app_current_fragment"
        private const val TEST_NOTIFICATION_ID = 1
    }
}