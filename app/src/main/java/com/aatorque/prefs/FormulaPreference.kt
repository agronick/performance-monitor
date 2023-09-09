package com.aatorque.prefs

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.preference.Preference
import com.aatorque.stats.R


// A subclass of PreferenceDialogFragmentCompat that corresponds to ScriptPreference
class FormulaPreference(context: Context, attributeSet: AttributeSet) : Preference(context, attributeSet) {
    lateinit var mSpinner: Spinner
    lateinit var mTextField: EditText
    val scriptNames: Array<String>
    val scripts: Array<String>
    var mText = ""
    // Bind the current script value to the dialog view

    init {
        // Get the arrays of script names and scripts from the resources
        scriptNames = context.resources.getStringArray(R.array.user_script_name)
        scripts = context.resources.getStringArray(R.array.user_script)
        val default = context.resources.getString(R.string.custom_expression)
        setSummaryProvider {
            valueToName(getValue(), default)
        }
    }

    fun valueToName(value: String, default: String =""): String {
        val pos = scripts.indexOf(value.trim())
        return if (pos != -1) {
            scriptNames[pos]
        } else {
            default
        }
    }

    fun onCreateDialogView(context: Context): View? {
        // Inflate the dialog layout
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.script_preference, null)

        // Get the spinner and the textbox from the view
        mSpinner = view.findViewById(R.id.spinner)
        mTextField = view.findViewById(R.id.text_field)
        mTextField.text = Editable.Factory.getInstance().newEditable(getValue())


        // Create an adapter for the spinner with the script names
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, scriptNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set the adapter and the listener for the spinner
        val selected = scripts.indexOf(getValue())
        mSpinner.setAdapter(adapter)
        mSpinner.setSelection(if (selected == -1) 0 else selected)
        mSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    // When an item is selected, update the textbox with the corresponding script
                    mTextField.setText(scripts[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        })
        return view
    }

    fun setValue(value: String) {
        mText = value
    }



    fun getValue(): String {
        return mText
    }

    override fun onClick() {
        super.onClick()
        AlertDialog.Builder(context).setView(
            onCreateDialogView(context)
        ).setPositiveButton(android.R.string.ok) { dialog, _ ->
            val txt = mTextField.text.toString()
            if (callChangeListener(txt)) {
                setValue(txt)
                notifyChanged()
            }
            dialog.dismiss()
        }.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
            .create()
            .show()
    }


}