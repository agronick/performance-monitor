package com.aatorque.stats;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;

import com.google.android.apps.auto.sdk.CarActivity;
import com.google.android.apps.auto.sdk.MenuItem;
import com.google.android.apps.auto.sdk.StatusBarController;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

import java.util.Objects;

public class MainCarActivity extends CarActivity{

    //menu stuff//

    static final String MENU_DASHBOARD = "dashboard";
  //  static final String MENU_CARINFO = "carinfo";
    static final String MENU_READINGS = "readings";
    static final String MENU_CREDITS = "credits";
    static final String MENU_STOPWATCH = "stopwatch";


    // static final String MENU_DEBUG_LOG = "log";
    // static final String MENU_DEBUG_TEST_NOTIFICATION = "test_notification";
    private static final String FRAGMENT_CAR = "dashboard";
    private static final String FRAGMENT_READINGS = "readings";
    private static final String FRAGMENT_CREDITS = "credits";
    private static final String FRAGMENT_STOPWATCH = "stopwatch";
    private static final String CURRENT_FRAGMENT_KEY = "app_current_fragment";

    private static final int TEST_NOTIFICATION_ID = 1;
    private String mCurrentFragmentTag;
    private Boolean connectivityOn, batteryOn, clockOn, micOn;
    private SharedPreferences preferences;
    private String selectedBackground;
    private final ListMenuAdapter.MenuCallbacks mMenuCallbacks = new ListMenuAdapter.MenuCallbacks() {
        @Override
        public void onMenuItemClicked(String name) {
            switch (name) {
                case MENU_DASHBOARD:
                    switchToFragment(FRAGMENT_CAR);
                    break;
                case MENU_STOPWATCH:
                    switchToFragment(FRAGMENT_STOPWATCH);
                    break;
                case MENU_CREDITS:
                    switchToFragment(FRAGMENT_CREDITS);
                    break;
            }
        }

        @Override
        public void onEnter() {
        }

        @Override
        public void onExit() {
            updateStatusBarTitle();
        }
    };
    //end menu stuff//
    private final FragmentManager.FragmentLifecycleCallbacks mFragmentLifecycleCallbacks
            = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            updateStatusBarTitle();
        }
    };
    private final Handler mHandler = new Handler();
    private final SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            preferenceChangeHandler();
        }
    };
    private String selectedTheme;

    @Override
    public void onResume() {
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onPause() {
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    private void preferenceChangeHandler() {
        // Do we really need this looks like old code?
        String readedBackground = preferences.getString("selectedBackground", "Black");
        if (!readedBackground.equals(selectedBackground)) {
            selectedBackground = readedBackground;
            int resId = getResources().getIdentifier(selectedBackground, "drawable", this.getPackageName());
            if (resId != 0) {
                Drawable wallpaperImage = getResources().getDrawable(resId);
                View container = findViewById(R.id.fragment_container);
                container.setBackgroundResource(R.drawable.background_incar_black);
                container.setBackground(wallpaperImage);
            }
        }

        String readedTheme = preferences.getString("selectedTheme", "VW GTI");
        if (!readedTheme.equals(selectedTheme)) {
            selectedTheme = readedTheme;
            setLocalTheme(selectedTheme);
            FragmentManager manager = getSupportFragmentManager();
            Fragment currentFragment = mCurrentFragmentTag == null ? null : manager.findFragmentByTag(mCurrentFragmentTag);
            if (currentFragment != null) {
                var trans = manager.beginTransaction().detach(currentFragment);
                if (Objects.equals(mCurrentFragmentTag, MENU_DASHBOARD)) {
                    trans.remove(currentFragment).add(R.id.fragment_container, new DashboardFragment(), FRAGMENT_CAR);
                } else {
                    trans.detach(currentFragment).attach(currentFragment);
                }
                trans.commit();
            }

            this.c().getDecorView().setSystemUiVisibility(
                    SYSTEM_UI_FLAG_FULLSCREEN |
                            SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            SYSTEM_UI_FLAG_IMMERSIVE | SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            setIgnoreConfigChanges(0xFFFF);
        }
            getCarUiController().getMenuController().setRootMenuAdapter(createMenu());

    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setLocalTheme("VW GTI");
        setContentView(R.layout.activity_car_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferenceChangeHandler();
/*
        CarUiController carUiController = getCarUiController();
        //force night mode
        carUiController.getStatusBarController().setDayNightStyle(DayNightStyle.FORCE_NIGHT);
*/
        FragmentManager fragmentManager = getSupportFragmentManager();

        //set fragments:
        CarFragment carfragment = new DashboardFragment();

        StopwatchFragment stopwatchfragment = new StopwatchFragment();
        CreditsFragment creditsfragment = new CreditsFragment();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, carfragment, FRAGMENT_CAR)
                .detach(carfragment)
                .add(R.id.fragment_container, stopwatchfragment, FRAGMENT_STOPWATCH)
                .detach(stopwatchfragment)
                .add(R.id.fragment_container, creditsfragment, FRAGMENT_CREDITS)
                .detach(creditsfragment)
                .commitNow();


        String initialFragmentTag = FRAGMENT_CAR;
        if (bundle != null && bundle.containsKey(CURRENT_FRAGMENT_KEY)) {
            initialFragmentTag = bundle.getString(CURRENT_FRAGMENT_KEY);
        }
        switchToFragment(initialFragmentTag);
        /* todo: maybe restore this
        MenuController menuController = getCarUiController().getMenuController();
        if (!preferences.getBoolean("rotaryInput", false)) {
            menuController.showMenuButton();
        } */
        StatusBarController statusBarController = getCarUiController().getStatusBarController();
        carfragment.setupStatusBar(statusBarController);

    }

    private ListMenuAdapter createMenu() {
        ListMenuAdapter mainMenu = new ListMenuAdapter();
        mainMenu.setCallbacks(mMenuCallbacks);

    //set menu
        mainMenu.addMenuItem(MENU_DASHBOARD, new MenuItem.Builder()
                .setTitle(getString(R.string.activity_main_title))
                .setType(MenuItem.Type.ITEM)
                .build());

        /*
        mainMenu.addMenuItem(MENU_READINGS, new MenuItem.Builder()
                .setTitle(getString(R.string.activity_readings_title))
                .setType(MenuItem.Type.ITEM)
                .build());
         */

        mainMenu.addMenuItem(MENU_STOPWATCH, new MenuItem.Builder()
                .setTitle(getString(R.string.activity_stopwatch_title))
                .setType(MenuItem.Type.ITEM)
                .build());


        mainMenu.addMenuItem(MENU_CREDITS, new MenuItem.Builder()
                .setTitle(getString(R.string.activity_credits_title))
                .setType(MenuItem.Type.ITEM)
                .build());


// 1 submenu item
     /*   ListMenuAdapter otherMenu = new ListMenuAdapter();
        otherMenu.setCallbacks(mMenuCallbacks);
        otherMenu.addMenuItem(MENU_DEMO, new MenuItem.Builder()
                .setTitle("Demo")
                .setType(MenuItem.Type.ITEM)
                .build());*/

        //   mainMenu.addSubmenu(MENU_OTHER, otherMenu);
        return mainMenu;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putString(CURRENT_FRAGMENT_KEY, mCurrentFragmentTag);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        switchToFragment(mCurrentFragmentTag);
    }

    private void setLocalTheme(String theme) {

        switch (theme) {
            case "VW GTI":
                setTheme(R.style.AppTheme_VolkswagenGTI);
                break;
            case "VW R/GTE":
                setTheme(R.style.AppTheme_VolkswagenGTE);
                break;
            case "VW":
                setTheme(R.style.AppTheme_Volkswagen);
                break;
            case "VW MIB2":
                setTheme(R.style.AppTheme_VolkswagenMIB2);
                break;
            case "VW AID":
                setTheme(R.style.AppTheme_VolkswagenAID);
                break;
            case "Seat Cupra":
                setTheme(R.style.AppTheme_SeatCupra);
                break;
            case "Cupra Division":
                setTheme(R.style.AppTheme_Cupra);
                break;
            case "Audi TT":
                setTheme(R.style.AppTheme_AudiTT);
                break;
            case "Seat":
                setTheme(R.style.AppTheme_Seat);
                break;
            case "Skoda":
                setTheme(R.style.AppTheme_Skoda);
                break;
            case "Skoda ONE":
                setTheme(R.style.AppTheme_SkodaOneApp);
                break;
            case "Skoda vRS":
                setTheme(R.style.AppTheme_SkodavRS);
                break;
            case "Skoda Virtual Cockpit":
                setTheme(R.style.AppTheme_SkodaVC);
                break;
            case "Audi":
                setTheme(R.style.AppTheme_Audi);
                break;
            case "Audi Virtual Cockpit":
                setTheme(R.style.AppTheme_AudiVC);
                break;
            case "Clubsport":
                setTheme(R.style.AppTheme_Clubsport);
                break;
            case "Minimalistic":
                setTheme(R.style.AppTheme_Minimalistic);
                break;
            case "Test":
                setTheme(R.style.AppTheme_Testing);
                break;
            case "Dark":
                setTheme(R.style.AppTheme_Dark);
                break;
            case "Mustang GT":
                setTheme(R.style.AppTheme_Ford);
                break;
            case "BMW":
                setTheme(R.style.AppTheme_BMW);
                break;

            default:
                // set default theme:
                setTheme(R.style.AppTheme_VolkswagenMIB2);
                break;
        }
    }

    private void switchToFragment(String tag) {
        if (tag.equals(mCurrentFragmentTag)) {
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        Fragment currentFragment = mCurrentFragmentTag == null ? null : manager.findFragmentByTag(mCurrentFragmentTag);
        Fragment newFragment = manager.findFragmentByTag(tag);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            transaction.detach(currentFragment);
        }
        if (newFragment != null) {
            transaction.attach(newFragment);
            transaction.commitAllowingStateLoss();
            mCurrentFragmentTag = tag;
        }
    }

    private void updateStatusBarTitle() {
        CarFragment fragment = (CarFragment) getSupportFragmentManager().findFragmentByTag(mCurrentFragmentTag);
        if(fragment!=null)
            getCarUiController().getStatusBarController().setTitle(fragment.getTitle());
    }
}