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
        setContentView(R.layout.activity_create_home)
        changeActivity()

    }

    private fun changeActivity() {
        if (userIsMemberOfGroup()){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    private fun userIsMemberOfGroup(): Boolean {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val docRef = Firebase.firestore.collection("users").document(userId!!)
        docRef.get().addOnCompleteListener { complete ->
            if (complete.isSuccessful) {
                val document = complete.result
                if (document != null) {
                    val houseRoles = document.data?.get("houseRoles")
                    println(houseRoles)
                    if (houseRoles != null) {
                        return@addOnCompleteListener
                    }
                }
            }
        }

        return false
    }
}