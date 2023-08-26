package com.mqbcoding.stats

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

class TorqueDisplay: Fragment() {
    val TAG = "TorqueDisplay"
    private var rootView: View? = null
    private var valueElement: TextView? = null
    private var iconElement: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView")
        val view = inflater.inflate(R.layout.fragment_display, container, false)
        rootView = view
        valueElement = view.findViewById(R.id.valueElement)
        iconElement = view.findViewById(R.id.iconElement)
        return rootView
    }
    // this sets all the labels/values in an initial state, depending on the chosen options
    fun setupElement(data: TorqueData) {
        val label = iconElement
        val value = valueElement

        if(label == null || value == null) return

        data.setNotifyUpdate {
            onUpdate(it)
        }
        var icon = ""
        label.setBackgroundResource(0)
        value.visibility = View.VISIBLE

        if (data.query == "none") {
            label.text = ""
            value.text = ""
            value.visibility = View.INVISIBLE
            icon = "empty"
        } else {
            label.text = data.shortName
            value.text = "-"
        }

        if (icon == "empty") {
            label.setBackgroundResource(0)
            val params = label.layoutParams as ConstraintLayout.LayoutParams
            params.width = 40
            label.layoutParams = params
        }
    }

    fun setupTypeface(typeface: Typeface) {
        valueElement?.typeface = typeface
        iconElement?.typeface = typeface
    }

    fun onUpdate(value: Double) {
        valueElement?.text = value.toString()
    }
}