package com.mqbcoding.stats

import android.app.Application
import android.content.Context
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.bigquery.BigqueryScopes
import com.mqbcoding.datastore.UserPreference
import com.mqbcoding.prefs.UserPreferenceSerializer
import dagger.hilt.android.HiltAndroidApp
import org.acra.config.toast
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Calendar
import java.util.Date
import java.util.logging.Level
import java.util.logging.Logger
import com.mqbcoding.stats.BuildConfig;
import org.acra.config.mailSender

@HiltAndroidApp
class App : Application() {
    var googleCredential: GoogleAccountCredential? = null
        private set
    private var defaultExceptionHandler: Thread.UncaughtExceptionHandler? = null

    override fun onCreate() {
        super.onCreate()
        /**
         * Logging level for HTTP requests/responses.
         *
         *
         *
         * To turn on, set to [Level.CONFIG] or [Level.ALL] and run this from command line:
         *
         *
         * <pre>
         * adb shell setprop log.tag.HttpTransport DEBUG
        </pre> *
         */
        Logger.getLogger("com.google.api.client").level = Level.OFF
        PreferenceManager.setDefaultValues(this, R.xml.settings, false)

        // Google Accounts
        val gc = GoogleAccountCredential.usingOAuth2(
            this,
            Arrays.asList(BigqueryScopes.BIGQUERY, BigqueryScopes.BIGQUERY_INSERTDATA)
        )
        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        gc.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null))
        googleCredential = gc
        // Save original exception handler before we change it

    }

    override fun attachBaseContext(base:Context) {
        super.attachBaseContext(base)

        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON
            toast {
                text = "Reporting crash"
            }
            mailSender {
                //required
                mailTo = "agronick+acra@gmail.com"
                //defaults to true
                reportAsFile = false
                //defaults to ACRA-report.stacktrace
                reportFileName = "Crash.txt"
            }
        }
    }

    companion object {
        const val PREF_ACCOUNT_NAME = "accountName"
        private fun getStackTrace(ex: Throwable): String {
            val sw = StringWriter()
            ex.printStackTrace(PrintWriter(sw))
            return sw.toString()
        }
    }
}