package com.aatorque.prefs

import android.content.Context
import android.util.AttributeSet


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.preference.ListPreference
import com.aatorque.stats.R



class ImageListPreference(
    context: Context,
    attrs: AttributeSet?
) : ListPreference(context, attrs) {

    data class CustomListItem(
        val title: String,
        val value: String,
        val iconRes: Int,
        var checked: Boolean
    )
    class CustomListAdapter(
        context: Context,
        private val layoutResource: Int,
        private val items: List<CustomListItem>,
    ) : ArrayAdapter<CustomListItem>(context, layoutResource, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(layoutResource, parent, false)
            val item = items[position]
            val icon = view.findViewById<ImageView>(R.id.icon)
            val title = view.findViewById<TextView>(R.id.title)
            icon.setImageResource(item.iconRes)
            title.text = item.title
            return view
        }

    }

    private var iconResArray: IntArray? = null
    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageListPreference)
        iconResArray = typedArray.getResourceId(R.styleable.ImageListPreference_entryImages, 0).let {
            if (it == 0) null else context.resources.obtainTypedArray(it).run {
                val array = IntArray(length())
                for (i in 0 until length()) {
                    array[i] = getResourceId(i, 0)
                }
                recycle()
                array
            }
        }
        typedArray.recycle()
    }

    override fun onClick() {
        val entries = entries ?: return
        val entryValues = entryValues
        val iconResArray = iconResArray ?: return

        if (entries.size != entryValues.size || entries.size != iconResArray.size) {
            throw IllegalStateException("Entries, entry values and icons must have the same size")
        }

        val items = entries.mapIndexed { index, title ->
            CustomListItem(
                title.toString(),
                entryValues[index].toString(),
                iconResArray[index],
                value == entryValues[index]
            )
        }

        val lv = ListView(context)
        val adapter = CustomListAdapter(context, R.layout.icon_list_row, items)

        AlertDialog.Builder(context)
            .setTitle(dialogTitle)
            .setView(lv)
            .setAdapter(adapter) { dialog, which ->
                setValueIndex(which)
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
