package com.mqbcoding.stats

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class FontFragmentBase: Fragment() {
    val selectedFont: String = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onPreferencesChangeHandler()
    }
    protected open fun onPreferencesChangeHandler() {
        val sharedPreferences = activity?.getSharedPreferences(
            "shared_preferences",
            Context.MODE_PRIVATE,
        ) ?: return
        val readedFont = sharedPreferences.getString("selectedFont", "segments")
        if (readedFont != selectedFont && readedFont != null) {
            val assetsMgr = context!!.assets
            var typeface = Typeface.createFromAsset(assetsMgr, "digital.ttf")
            when (selectedFont) {
                "segments" -> typeface = Typeface.createFromAsset(assetsMgr, "digital.ttf")
                "seat" -> typeface =
                    Typeface.createFromAsset(assetsMgr, "SEAT_MetaStyle_MonoDigit_Regular.ttf")

                "audi" -> typeface = Typeface.createFromAsset(assetsMgr, "AudiTypeDisplayHigh.ttf")
                "vw" -> typeface = Typeface.createFromAsset(assetsMgr, "VWTextCarUI-Regular.ttf")
                "vw2" -> typeface = Typeface.createFromAsset(assetsMgr, "VWThesis_MIB_Regular.ttf")
                "frutiger" -> typeface = Typeface.createFromAsset(assetsMgr, "Frutiger.otf")
                "vw3" -> typeface = Typeface.createFromAsset(assetsMgr, "VW_Digit_Reg.otf")
                "skoda" -> typeface = Typeface.createFromAsset(assetsMgr, "Skoda.ttf")
                "larabie" -> typeface = Typeface.createFromAsset(assetsMgr, "Larabie.ttf")
                "ford" -> typeface = Typeface.createFromAsset(assetsMgr, "UnitedSans.otf")
            }
            setupTypeface(typeface)
        }
    }

    abstract fun setupTypeface(name: Typeface)
}