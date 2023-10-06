package com.msprysak.rentersapp.util

import android.app.Activity
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.findNavController

fun createClickableSpan(textView: TextView, clickableText: String, destinationAction: NavDirections) {
    val spannableString = SpannableStringBuilder(textView.text)

    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            widget.findNavController().navigate(destinationAction)
        }
    }

    val startIndex = textView.text.indexOf(clickableText)
    val endIndex = startIndex + clickableText.length

    textView.setTextIsSelectable(false)
    textView.setLongClickable(false)

    spannableString.setSpan(clickableSpan, startIndex, endIndex, 0)

    textView.text = spannableString
    textView.movementMethod = LinkMovementMethod.getInstance()
}

