package com.msprysak.rentersapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.msprysak.rentersapp.ui.login.LoginFragment

class MainActivity : AppCompatActivity(), FragmentNavigation {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .add(
                R.id.container,
                RegisterFragment()
            )
            .commit()
    }

    override fun navigateFrag(fragment: Fragment, addToStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.container, LoginFragment())

        if (addToStack){
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}
