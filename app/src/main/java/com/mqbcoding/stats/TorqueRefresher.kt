package com.mqbcoding.stats
import android.util.Log
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

    fun populateQuery(pos: Int, query: String) {
        data[pos]?.notifyUpdate = null
        data[pos] = TorqueData(query)
        Log.d(TAG, "Setting query: $query for pos $pos")
    }

    fun refreshQueries(service: TorqueService, runOnUiThread: (action: Runnable) -> Unit) {
        val needRefresh = data.filter { it.value.pid != null }
        if (needRefresh.isNotEmpty()) {
            val refreshKeys = needRefresh.map { it.key }
            val asPids = needRefresh.map { it.value.pid }
            service.addConnectCallback {
                val pidData = it.getPIDValuesAsDouble(convertPids(asPids))
                runOnUiThread(Runnable {
                    refreshKeys.forEachIndexed { idx, i ->
                        val elm = data[i]!!
                        val info = pidData[idx]
                        elm.setLastData(info)
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
            val asPids = needRefresh.map { it.value.pid }
            //  "<longName>,<shortName>,<unit>,<maxValue>,<minValue>,<scale>",
            service.addConnectCallback {
                val pidInfo = it.getPIDInformation(convertPids(asPids)).map { it.split(",") }
                if (pidInfo.size != needRefresh.size) {
                    Log.e(TAG, "Mismatched request response size ${needRefresh.size}:${pidInfo.size}")
                    return@addConnectCallback
                }
                refreshKeys.forEachIndexed { idx, i ->
                    val elm = data[i]!!
                    val info = pidInfo[idx]
                    elm.longName = info[0]
                    elm.shortName = info[1]
                    elm.unit = info[2]
                    elm.maxValue = info[3].toInt()
                    elm.minValue = info[4].toInt()
                    elm.scale = info[5].toFloat()
                    notifyDone(i,  elm)
                }
                hasLoadedData = true
            }
        }
    }

    fun hasChanged(idx: Int, readedElementQuery: String?): Boolean {
        if (!data.containsKey(idx)) return true;
        return data[idx]?.query != readedElementQuery
    }

    fun convertPids(items: List<String?>): Array<String> {
        return items.map {
            if (it == null) {
                throw RuntimeException("cannot convert null pid")
            }
            return@map it
        }.toTypedArray()
    }
}