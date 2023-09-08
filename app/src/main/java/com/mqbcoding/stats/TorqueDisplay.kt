package com.mqbcoding.stats

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.icu.text.NumberFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

class TorqueDisplay : Fragment() {
    val TAG = "TorqueDisplay"
    private var rootView: View? = null
    private var valueElement: TextView? = null
    private var iconElement: TextView? = null
    private var unit = ""
    private var numberFormatter = NumberFormat.getInstance()


    init {
        numberFormatter.maximumFractionDigits = 2
        numberFormatter.minimumFractionDigits = 0
        numberFormatter.isGroupingUsed = true
    }

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
        unit = data.display.unit

        if(label == null || value == null) return

        data.notifyUpdate = this::onUpdate

        var icon = data.getDrawableName()
        label.setBackgroundResource(0)
        value.visibility = View.VISIBLE

        if (data.pid == null) {
            label.text = ""
            value.text = ""
            value.visibility = View.INVISIBLE
            icon = "empty"
        } else {
            if (icon == null) {
                label.text = data.display.label
            } else {
                label.text = ""
                label.setBackgroundResource(
                    resources.getIdentifier(
                        icon,
                        "drawable",
                        requireContext().packageName,
                    )
                )
            }
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

    @SuppressLint("SetTextI18n")
    fun onUpdate(data: TorqueData) {
        valueElement?.text = if (data.lastDataStr != null) {
            data.lastDataStr + unit
        } else {
            "${numberFormatter.format(data.lastData)}${unit}"
        }
    }

    fun bottomDisplay() {
        val params = iconElement!!.layoutParams as ConstraintLayout.LayoutParams
        params.bottomToTop = params.topToBottom
        params.topToBottom = ConstraintLayout.LayoutParams.UNSET
        iconElement!!.requestLayout()
    }
}