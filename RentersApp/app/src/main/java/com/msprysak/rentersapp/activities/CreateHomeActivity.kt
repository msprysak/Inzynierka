package com.msprysak.rentersapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.msprysak.rentersapp.R

class CreateHomeActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userIsMemberOfGroup { isMember ->
            if (isMember) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else{
                setContentView(R.layout.activity_create_home)
            }
        }

    }

    private fun userIsMemberOfGroup(callback: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val docRef = Firebase.firestore.collection("users").document(userId)

            docRef.get().addOnCompleteListener { complete ->
                if (complete.isSuccessful) {
                    val document = complete.result
                    val houseRoles = document?.data?.get("houseRoles")
                    val isMember = houseRoles != null
                    callback(isMember)
                } else {
                    callback(false)
                }
            }
        } else {
            callback(false)
        }
    }



}