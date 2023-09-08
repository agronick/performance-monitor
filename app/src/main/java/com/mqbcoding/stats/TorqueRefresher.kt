package com.mqbcoding.stats
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.mqbcoding.datastore.Display
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class TorqueRefresher {
    val TAG = "TorqueRefresher"
    val data = HashMap<Int, TorqueData>()
    private val executor = ScheduledThreadPoolExecutor(7)
    val handler = Handler(Looper.getMainLooper())

    fun populateQuery(pos: Int, query: Display): TorqueData {
        data[pos]?.notifyUpdate = null
        val td = TorqueData(query)
        data[pos] = td
        Log.d(TAG, "Setting query: $query for pos $pos")
        return td
    }

    fun makeExecutors(service: TorqueService) {
        data.values.forEachIndexed { index, torqueData ->
            val refreshOffset = (250L / data.size) * index
            if (torqueData.pid != null && torqueData.refreshTimer == null) {
                torqueData.refreshTimer = executor.scheduleAtFixedRate({
                    service.addConnectCallback { ts ->
                        val value = ts.getValueForPid(torqueData.pidInt!!, true)
                        torqueData.setLastData(value.toDouble())
                        handler.post {
                            torqueData.sendNotifyUpdate()
                        }
                    }
                }, refreshOffset, 250L, TimeUnit.MILLISECONDS)
            }
        }
    }

    fun stopExecutors() {
        for (td in data.values) {
            td.refreshTimer?.cancel(true)
            td.refreshTimer = null
        }
    }

    fun hasChanged(idx: Int, otherScreen: Display?): Boolean {
        if (!data.containsKey(idx)) return true
        return data[idx]?.display?.equals(otherScreen) != true
    }

}