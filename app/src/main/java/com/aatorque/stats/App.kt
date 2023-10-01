package com.aatorque.stats

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import org.acra.config.mailSender
import org.acra.config.toast
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import timber.log.Timber
import timber.log.Timber.*


class App : Application() {

    val logTree = CacheLogTree()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(logTree)
        PreferenceManager.setDefaultValues(this, R.xml.settings, false)
    }

    override fun attachBaseContext(base:Context) {
        super.attachBaseContext(base)

        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.KEY_VALUE_LIST
            toast {
                text = "App crashed. Tap to report."
            }
            mailSender {
                //required
                mailTo = "zgronick+zcrz@gmzil.com".replace("z", "a")
                //defaults to true
                reportAsFile = false
                //defaults to ACRA-report.stacktrace
                reportFileName = "Crash.txt"
            }
        }
    }
}