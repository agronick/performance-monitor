package com.aatorque.stats

import android.app.Application
import android.content.Context
import org.acra.config.mailSender
import org.acra.config.toast
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import timber.log.Timber


class App : Application() {

    val logTree = CacheLogTree()


    override fun onCreate() {
        super.onCreate()
        Timber.plant(logTree)
        fixAndroid14Perms()
    }
    
    fun fixAndroid14Perms() {
        for (file in getDir("car_sdk_impl", Context.MODE_PRIVATE).listFiles() ?: emptyArray()) {
            if (file.isDirectory) {
                for (subfile in file.listFiles() ?: emptyArray()) {
                    Timber.i("Setting read only permission for $subfile")
                    subfile.setReadOnly()
                }
            }
            Timber.i("Setting read only permission for $file")
            file.setReadOnly()
        }
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