package com.msprysak.rentersapp.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.data.model.Request
import com.msprysak.rentersapp.data.model.User

class FirebaseRepository {
    private val DEBUG = "FirebaseRepository_DEBUG"

    private val auth = FirebaseAuth.getInstance()
    private val cloud = FirebaseFirestore.getInstance()
    val sharedPremisesData: MutableLiveData<Premises> = MutableLiveData()
    val sharedUserData: MutableLiveData<User> = MutableLiveData()
    private val joinRequests: MutableLiveData<List<Request>> = MutableLiveData()

    fun fetchUserData() {
        val docRef = cloud.collection("users")
            .document(auth.currentUser!!.uid)

        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    sharedUserData.postValue(user!!)
                } else {
                    Log.d(DEBUG, "fetchUserData: Document doesn't exist")
                }
            }
    }
    fun fetchPremisesData() {
        val docRef = cloud.collection("premises")
            .document(sharedUserData.value?.houseRoles?.keys?.first()!!)
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val premises = documentSnapshot.toObject(Premises::class.java)
                    sharedPremisesData.postValue(premises!!)
                    Log.d(DEBUG, "fetchPremisesData: Success")
                } else {
                    Log.d(DEBUG, "fetchPremisesData: Document doesn't exist")
                }
            }
    }

    fun fetchJoinRequests() {
        val docRef = cloud.collection("requests")
            .whereEqualTo("premisesId", sharedUserData.value?.houseRoles?.keys?.first()!!)
            .whereEqualTo("status", "pending")

        docRef.get()
            .addOnSuccessListener { querySnapshot ->
                val requests = mutableListOf<Request>()
                for (document in querySnapshot) {
                    val request = document.toObject(Request::class.java)
                    requests.add(request)
                }
                joinRequests.postValue(requests)
            }

    }
}
