package com.mqbcoding.stats

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.github.martoreto.aauto.vex.CarStatsClient
import com.github.martoreto.aauto.vex.FieldSchema
import com.google.android.apps.auto.sdk.StatusBarController
import com.mqbcoding.stats.CarStatsService.CarStatsBinder
import org.prowl.torque.remote.ITorqueService
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class ReadingsViewFragment : CarFragment() {
    private val TAG = "ReadingsViewFragment"
    private val mLastMeasurements: MutableMap<String, Any> = HashMap()
    private val mSchema = HashMap<String, FieldSchema>()
    private var mHandler: Handler? = null
    private var updateTimerRunnable: Runnable? = null
    private var adapter = HashMapAdapter()
    private var updateTimer: Timer? = null
    private var translationsMap: HashMap<String, String>? = null
    private var mOmitEmptyEntries = false

    internal inner class HashMapAdapter : BaseAdapter() {
        private val mData = LinkedHashMap<String, Any>()
        private var mSchema: Map<String, FieldSchema>? = null
        private var mKeys: Array<String> = arrayOf()
        fun putAll(values: Map<String, Any>?) {
            mData.putAll(values!!)
            mKeys = mData.keys.toTypedArray()
            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return mData.size
        }

        override fun getItem(position: Int): Any {
            return mData[mKeys[position]]!!
        }

        fun getKey(position: Int): String {
            return mKeys[position]
        }

        override fun getItemId(arg0: Int): Long {
            return arg0.toLong()
        }

        override fun getView(pos: Int, convertView: View, parent: ViewGroup): View {
            var convertView = convertView
            val key = mKeys[pos]
            val obj = getItem(pos)
            var value = ""
            if (obj != null) {
                value = getItem(pos).toString()
            }
            var translated: String? = key
            if (translationsMap != null && translationsMap!!.containsKey(key)) translated =
                translationsMap!![key]
            if (convertView == null) {
                convertView =
                    LayoutInflater.from(context).inflate(R.layout.list_readings_item, parent, false)
            }
            val tvName = convertView.findViewById<TextView>(R.id.tvName)
            val tvValue = convertView.findViewById<TextView>(R.id.tvValue)
            val tvUnit = convertView.findViewById<TextView>(R.id.tvUnit)
            tvName.text = translated
            tvValue.text = value
            var unit: String? = ""
            if (mSchema != null) {
                val field = mSchema!![key]
                if (field != null) {
                    val receivedUnit = field.unit
                    if (receivedUnit != null && !receivedUnit.isEmpty()) {
                        unit = receivedUnit
                    }
                }
            }
            tvUnit.text = unit
            return convertView
        }

        fun putSchema(schema: Map<String, FieldSchema>?) {
            mSchema = schema
            notifyDataSetChanged()
        }
    }

    override fun onPause() {
        Log.i(TAG, "onDeactivate")
        updateTimer!!.cancel()
        super.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView")
        val rootView = inflater.inflate(R.layout.fragment_readings, container, false)
        mHandler = Handler(Looper.getMainLooper())
        adapter = HashMapAdapter()
        val listView = rootView.findViewById<ListView>(R.id.lvItems)
        mHandler!!.post {
            listView.adapter = adapter
            listView.onItemClickListener = listClickListener
        }

        //todo: Finalize this.
        val schemaItem1 = HashMap<String, FieldSchema>()
        val tempMap = HashMap<String, Any>()
        schemaItem1["No data received"] = FieldSchema(0, "No data", "", 0f, 0f, 0f)
        tempMap["No Data"] = "0"
        schemaItem1["No connection"] = FieldSchema(0, "Test", "V", 0f, 0f, 0f)
        tempMap["No connection"] = "0.00"
        schemaItem1["batteryVoltage3"] = FieldSchema(0, "Test", "V", 0f, 0f, 0f)
        tempMap["batteryVoltage3"] = "0"
        mSchema.putAll(schemaItem1)
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(TAG, "onAttach")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.registerOnSharedPreferenceChangeListener(mPreferencesListener)
        readPreferences(sharedPreferences)
    }

    override fun setupStatusBar(sc: StatusBarController) {}

    private val mPreferencesListener =
        OnSharedPreferenceChangeListener { sharedPreferences, s -> readPreferences(sharedPreferences) }

    private fun readPreferences(preferences: SharedPreferences) {
        mOmitEmptyEntries = preferences.getBoolean(PREF_OMIT_EMPTY_ENTRIES, true)
    }

    private val listClickListener = OnItemClickListener { parent, view, position, id ->
        if (!mSchema.isEmpty()) {
            val bundle = Bundle()
            bundle.putSerializable(ExlapItemDetailsFragment.ARG_SCHEMA, mSchema)
            val itemKey = adapter.getKey(position)
            bundle.putString(ExlapItemDetailsFragment.ARG_SELECTED_KEY, itemKey)
            val detailsFragment = ExlapItemDetailsFragment()
            detailsFragment.arguments = bundle
            val fragmentManager = fragmentManager
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, detailsFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    companion object {
        const val PREF_OMIT_EMPTY_ENTRIES = "omitEmptyEntries"
    }
}