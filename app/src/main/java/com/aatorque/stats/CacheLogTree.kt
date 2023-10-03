package com.aatorque.stats

import android.provider.Settings.Global
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

class CacheLogTree: Timber.DebugTree() {

    companion object {
        const val KEEP_LOGS = 1_000
    }

    data class LogDesc(
        val priority: Int,
        val tag: String?,
        val message: String,
        val throwable: Throwable?,
    )

    val logCache = ArrayDeque<LogDesc>(KEEP_LOGS)
    val logLock = Mutex()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        GlobalScope.launch {
            if (BuildConfig.DEBUG) {
                super.log(priority, tag, message, t)
            }
            logCache.add(
                LogDesc(priority, tag, message, t)
            )
            logLock.withLock {
                while (logCache.size > KEEP_LOGS) {
                    logCache.removeFirst()
                }
            }
        }
    }

    fun logToString(): Array<String> {
        val logLines = runBlocking {
            logLock.withLock {
                logCache.map {
                    val level = when (it.priority) {
                        Log.ERROR -> "Error"
                        Log.DEBUG -> "Debug"
                        Log.WARN -> "Warn"
                        Log.INFO -> "Info"
                        Log.VERBOSE -> "Verbose"
                        Log.ASSERT -> "Assert"
                        else -> "Unknown"
                    }
                    val trace = it.throwable?.stackTraceToString() ?: ""
                    return@map "${it.tag ?: ""}: $level ${it.message} $trace"
                }.toTypedArray()
            }
        }
        return logLines
    }
}