package com.mqbcoding.stats;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.bigquery.BigqueryScopes;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class App extends Application {
    static final String PREF_ACCOUNT_NAME = "accountName";

    private GoogleAccountCredential mCredential;
    private Thread.UncaughtExceptionHandler defaultExceptionHandler;


    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * Logging level for HTTP requests/responses.
         *
         * <p>
         * To turn on, set to {@link Level#CONFIG} or {@link Level#ALL} and run this from command line:
         * </p>
         *
         * <pre>
         adb shell setprop log.tag.HttpTransport DEBUG
         * </pre>
         */
        Logger.getLogger("com.google.api.client").setLevel(Level.OFF);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        // Google Accounts
        mCredential =
                GoogleAccountCredential.usingOAuth2(this,
                        Arrays.asList(BigqueryScopes.BIGQUERY, BigqueryScopes.BIGQUERY_INSERTDATA));
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        mCredential.setSelectedAccountName(settings.getString(App.PREF_ACCOUNT_NAME, null));
        // Save original exception handler before we change it
        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                createCrashDump(t, e);
            }
        });
    }

    private void createCrashDump(Thread t, Throwable e) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH_mm_ss");
        String path = Environment.getExternalStorageDirectory() + "/CarLogs/";
        String fullName = path + "crashlog_" + sdf.format(new Date()) + ".log";
        File file = new File (fullName);
        FileWriter writer;
        try {
            writer = new FileWriter(file);
            writer.write("EXCEPTION OCCURRED ON " + Calendar.getInstance().getTime() + "!\n");
            writer.write(getStackTrace(e));
            writer.write("-----\n");
            writer.close();
        } catch (Exception ex) {
            Log.e("App", "uncaughtException: " + e.getLocalizedMessage());
        }
        defaultExceptionHandler.uncaughtException(t, e);
    }

    private static String getStackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public GoogleAccountCredential getGoogleCredential() {
        return mCredential;
    }
}
