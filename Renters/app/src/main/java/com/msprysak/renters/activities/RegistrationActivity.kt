package com.msprysak.renters.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.msprysak.renters.R

class RegistrationActivity : AppCompatActivity() {
    private val fbAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        FirebaseApp.initializeApp(this)
    }

    override fun onStart() {
        super.onStart()
        isCurrentUser()
    }

    // Function isCurrentUser() checks if the user is already logged in. If the user is logged in, the user is redirected to the MainActivity.
    private fun isCurrentUser() {
        fbAuth.currentUser?.let { auth ->
            val intent = Intent(applicationContext, CreateHomeActivity::class.java).apply {
//                Flags FLAG_ACTIVITY_NEW_TASK and FLAG_ACTIVITY_CLEAR_TASK are used to clear the back stack of activities.
                flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
        }
    }
}