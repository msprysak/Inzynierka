package com.msprysak.rentersapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.msprysak.rentersapp.R

class RegistrationActivity : AppCompatActivity() {

    private val fbAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
    }


    override fun onStart() {
        super.onStart()
        isCurrentUser()
    }

// Function isCurrentUser() checks if the user is already logged in. If the user is logged in, the user is redirected to the MainActivity.
    private fun isCurrentUser() {
        fbAuth.currentUser?.let { auth ->
            val intent = Intent(this, CreateHomeActivity::class.java).apply{
                flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)


        }
    }
}



