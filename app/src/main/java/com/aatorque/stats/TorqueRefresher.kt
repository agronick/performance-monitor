package com.aatorque.stats
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.aatorque.datastore.Display
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class TorqueRefresher {
    val TAG = "TorqueRefresher"
    val data = HashMap<Int, TorqueData>()
    private val executor = ScheduledThreadPoolExecutor(7)
    val handler = Handler(Looper.getMainLooper())
    var lastConnectStatus: Boolean? = null
    var conWatcher: ((Boolean) -> Unit)? = null

    fun populateQuery(pos: Int, query: Display): TorqueData {
        data[pos]?.stopRefreshing(true)
        val td = TorqueData(query)
        data[pos] = td
        Log.d(TAG, "Setting query: $query for pos $pos")
        return td
    }

    fun makeExecutors(service: TorqueService) {
        data.values.forEachIndexed { index, torqueData ->
            val refreshOffset = (400L / data.size) * index
            if (torqueData.pid != null && torqueData.refreshTimer == null) {
                Log.d(TAG, "Scheduled item in position $index with $refreshOffset delay")
                doRefresh(service, torqueData)
                torqueData.refreshTimer = executor.scheduleAtFixedRate({
                    doRefresh(service, torqueData)
                }, refreshOffset, 400L, TimeUnit.MILLISECONDS)
            } else {
                Log.d(TAG, "No reason to schedule item in position $index")
            }
        }
    }

    fun doRefresh(service: TorqueService, torqueData: TorqueData) {
        service.runIfConnected { ts ->
            val value = ts.getPIDValuesAsDouble(arrayOf( torqueData.pid!!))[0]
            torqueData.lastData = value
            if (value != 0.0 || torqueData.hasReceivedNonZero) {
                handler.post {
                    torqueData.sendNotifyUpdate()
                    if (value != 0.0 && lastConnectStatus != true) {
                        lastConnectStatus = true
                        torqueData.hasReceivedNonZero = true
                        conWatcher?.let { it(true) }
                    }
                }
            }
        }
    }

    fun stopExecutors() {
        for (td in data.values) {
            td.stopRefreshing()
        }
    }

    fun hasChanged(idx: Int, otherScreen: Display?): Boolean {
        if (!data.containsKey(idx)) return true
        return data[idx]?.display?.equals(otherScreen) != true
    }

    fun watchConnection(service: TorqueService, notifyConState: (connected: Boolean?) -> Unit) {
        notifyConState(lastConnectStatus)
        service.addConnectCallback {
            if (lastConnectStatus == null) {
                lastConnectStatus = null
                notifyConState(false)
            }
            conWatcher = notifyConState
        }
    }

}