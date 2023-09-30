package com.msprysak.rentersapp

import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.navGraphViewModels
import com.msprysak.rentersapp.ui.login.LoginFragment


class RegisterFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_register, container, false)

        val textView = rootView.findViewById<TextView>(R.id.accountSetTextView)


        val spannableString = SpannableString(textView.text);

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                var navRegister = activity as FragmentNavigation

                navRegister.navigateFrag(LoginFragment(), true)
            }
        }

        val startIndex = textView.text.indexOf("Zaloguj się!")
        val endIndex = startIndex + "Zaloguj się!".length
        spannableString.setSpan(clickableSpan, startIndex, endIndex, 0)

        textView.text = spannableString
        textView.setTextIsSelectable(false)
        textView.movementMethod = LinkMovementMethod.getInstance()


        return rootView;
    }


}