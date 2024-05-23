package com.msprysak.rentersapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.UserRepositoryInstance

class CreateHomeActivity : AppCompatActivity() {
    private val repository = UserRepositoryInstance.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository.fetchUserData()
        userIsMemberOfGroup { isMember ->
            if (isMember) {
                changeActivity()
            } else {
                setContentView(R.layout.activity_create_home)
            }
        }
    }

    private fun changeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        this.startActivity(intent)
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
