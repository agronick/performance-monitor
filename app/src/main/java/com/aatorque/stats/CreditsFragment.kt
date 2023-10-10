package com.aatorque.stats

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import timber.log.Timber

class CreditsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.i("onCreateView")
        val view = inflater.inflate(R.layout.fragment_credits, container, false)
        view.findViewById<View>(R.id.githubBtn).setOnClickListener { _: View? ->
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/agronick/aa-torque"))
            startActivity(intent)
        }
        view.findViewById<View>(R.id.donateBtn).setOnClickListener { _: View? ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/kagronick"))
            startActivity(intent)
        }
        val iconCredits = view.findViewById<LinearLayout>(R.id.ic_credits_list)
        resources.getStringArray(R.array.ic_credits_items).forEach {
            val tv = TextView(requireContext())
            tv.text = Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
            iconCredits.addView(tv)
        }
        return view
    }
}