package com.mqbcoding.stats
import android.util.Log
import com.mqbcoding.datastore.Display
import com.mqbcoding.datastore.Screen
import java.lang.RuntimeException
import java.math.BigInteger

class TorqueRefresher {
    val TAG = "TorqueRefresher"
    val data = HashMap<Int, TorqueData>()
    var hasLoadedData = false

    companion object {
        fun isTorqueQuery(query: String?): Boolean {
            if (query == null) return false
            return query.startsWith("torque")
        }
    }

    fun populateQuery(pos: Int, query: Display): TorqueData {
        data[pos]?.notifyUpdate = null
        val td = TorqueData(query)
        data[pos] = td
        Log.d(TAG, "Setting query: $query for pos $pos")
        return td
    }

    fun refreshQueries(service: TorqueService, runOnUiThread: (action: Runnable) -> Unit) {
        val needRefresh = data.filter { it.value.pid != null }
        if (needRefresh.isNotEmpty()) {
            service.addConnectCallback { ts ->
                runOnUiThread(Runnable {
                    needRefresh.forEach {
                        val pidData = ts.getValueForPid(it.value.pidInt!!, true)
                        it.value.setLastData(pidData.toDouble())
                    }
                })
            }
        }
    }

    fun refreshInformation(service: TorqueService, notifyDone: (pos: Int, data: TorqueData)-> Unit) {
        hasLoadedData = false
        val needRefresh = data.filter { it.value.pid != null }
        if (needRefresh.isNotEmpty()) {
            val refreshKeys = needRefresh.map { it.key }
            //  "<longName>,<shortName>,<unit>,<maxValue>,<minValue>,<scale>",
            service.addConnectCallback {
                refreshKeys.forEachIndexed { idx, i ->
                    val elm = data[i]!!
                    notifyDone(i,  elm)
                }
                hasLoadedData = true
            }
        }
    }

    fun hasChanged(idx: Int, otherScreen: Display?): Boolean {
        if (!data.containsKey(idx)) return true
        return data[idx]?.display?.equals(otherScreen) != true
    }

}