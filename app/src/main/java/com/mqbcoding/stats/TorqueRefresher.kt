package com.mqbcoding.stats
import android.util.Log
import androidx.javascriptengine.JavaScriptIsolate
import androidx.javascriptengine.JavaScriptSandbox
import com.mqbcoding.datastore.Display
import com.mqbcoding.datastore.Screen
import java.lang.RuntimeException
import java.math.BigInteger

class TorqueRefresher {
    val TAG = "TorqueRefresher"
    val data = HashMap<Int, TorqueData>()
    var jsSandbox: JavaScriptIsolate? = null

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
                        it.value.setLastData(pidData.toDouble(), jsSandbox)
                    }
                })
            }
        }
    }

    fun hasChanged(idx: Int, otherScreen: Display?): Boolean {
        if (!data.containsKey(idx)) return true
        return data[idx]?.display?.equals(otherScreen) != true
    }

}