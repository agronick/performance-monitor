package com.mqbcoding.stats
import org.prowl.torque.remote.ITorqueService
import java.math.BigInteger

class TorqueRefresher {
    val data = HashMap<Int, TorqueData>()

    companion object {
        fun isTorqueQuery(query: String?): Boolean {
            if (query == null) return false
            return query.startsWith("torque")
        }
    }

    fun populateQuery(pos: Int, dataItem: TorqueData?) {
        if (dataItem == null) {
            data.remove(pos)
        } else {
            data[pos] = dataItem
        }
    }

    fun refreshQueries(service: ITorqueService) {
        val fetchQueries = arrayListOf<String>()
        val indexInfo = arrayListOf<Int>()
        val fetchData = arrayListOf<String>()
        val indexData = arrayListOf<Int>()
        for (i in data.keys) {
            if (data[i] != null) {
                if (data[i]?.pidInfo == null) {
                    data[i]!!.pid?.let { fetchQueries.add(it) }
                    indexInfo.add(i)
                }
                data[i]!!.pid?.let { fetchData.add(it) }
                indexData.add(i)
            }
        }
        if (fetchQueries.isNotEmpty()) {
            val pidInfo =
                service.getPIDInformation(fetchQueries.toArray(arrayOfNulls(fetchQueries.size)))
            for (i in indexInfo) {
                data[i]?.pidInfo = pidInfo[i]
            }
        }
        if (fetchData.isNotEmpty()) {
            val pidData = service.getPIDValuesAsDouble(fetchData.toArray(arrayOfNulls(fetchQueries.size)))
            for (i in indexData) {
                data[i]?.lastData = pidData[i]
            }
        }
    }

    fun getByQuery(query: String) {
        for (pkg in data.values) {

        }
    }
}