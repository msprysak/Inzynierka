package com.msprysak.rentersapp.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.msprysak.rentersapp.interfaces.CallBack
import com.msprysak.rentersapp.data.model.Request
import com.msprysak.rentersapp.data.model.User

class JoinRequestRepository(private val userData: LiveData<User>)
     {

         private val premisesRepository = PremisesRepository.getInstance(userData)
    private val DEBUG = "JoinRequestRepository_DEBUG"

    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cloud = FirebaseFirestore.getInstance()
         val requestData: LiveData<List<Request>> = MutableLiveData()


     fun joinRequestListener(): LiveData<List<Request>> {
        val houseRoles = userData.value?.houseRoles
        if (!houseRoles.isNullOrEmpty()) {
            val premisesId = premisesRepository.premises.value!!.premisesId!!
            val docRef = cloud.collection("requests")
                .whereEqualTo("premisesId", premisesId)
            docRef.addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.d(DEBUG, "getJoinRequests: ${e.message}")
                    // Dodaj obsługę błędów tutaj, jeśli to konieczne.
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val requests = mutableListOf<Request>()
                    for (document in querySnapshot) {
                        val request = document.toObject(Request::class.java)
                        requests.add(request)
                    }
                    (requestData as MutableLiveData).postValue(requests)
                }
            }
        }
        return requestData
    }

     fun acceptRequest(request: Request, callback: CallBack) {
        val requestCollectionRef = cloud.collection("requests")
            .whereEqualTo("premisesId", request.premisesId)
            .whereEqualTo("userId", request.userId)
        val premisesDocRef = cloud.collection("premises")
            .document(request.premisesId)

        requestCollectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val requestDocRef = querySnapshot.documents[0].reference

                    cloud.runTransaction { transaction ->
                        // Odczytaj dane z requestDocRef i premisesDocRef
                        val requestData = transaction.get(requestDocRef)
                        val userId = requestData.getString("userId")
                        val premisesId = requestData.getString("premisesId")
                        val premisesData = transaction.get(premisesDocRef)
                        val users = premisesData.get("users") as MutableMap<*, *>
                        val newUsers = users.toMutableMap()
                        newUsers[userId!!] = "tenant"

                        // Wykonaj operacje zapisu
                        transaction.delete(requestDocRef)
                        transaction.update(premisesDocRef, "users", newUsers)

                        val userDocRef = cloud.collection("users").document(userId)
                        transaction.update(userDocRef, "houseRoles.$premisesId", "tenant")

                        null
                    }
                        .addOnSuccessListener {
                            callback.onSuccess()
                        }
                        .addOnFailureListener { e ->
                            callback.onFailure("Ups, coś poszło nie tak")
                        }
                } else {
                    callback.onFailure("Brak pasujących żądań")
                }
            }
            .addOnFailureListener { e ->
                callback.onFailure("Ups, coś poszło nie tak")
            }
    }

     fun rejectRequest(request: Request, callback: CallBack) {
        val requestRef = cloud.collection("requests")
            .whereEqualTo("premisesId", request.premisesId)
            .whereEqualTo("userId", request.userId)

        requestRef.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    documentSnapshot.reference.delete()
                    callback.onSuccess()
                } else {
                    callback.onFailure("Ups, coś poszło nie tak")
                }
            }
            .addOnFailureListener { e ->
                Log.d(DEBUG, "rejectJoinRequest: ${e.message}")
                callback.onFailure("Ups, coś poszło nie tak")
            }
    }

     fun sendJoinRequest(randomCode: String, callback: CallBack) {
        val userId = auth.currentUser?.uid
        val temporaryCodesRef = cloud.collection("temporaryCodes")

        temporaryCodesRef.whereEqualTo("code", randomCode)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    val premisesId = documentSnapshot.getString("premisesId")
                    if (userId != null && premisesId != null) {
                        val requestsRef = cloud.collection("requests")

                        requestsRef.whereEqualTo("premisesId", premisesId)
                            .whereEqualTo("userId", userId)
                            .get()
                            .addOnSuccessListener { existingRequestsSnapshot ->
                                if (existingRequestsSnapshot.isEmpty) {
                                    val request = hashMapOf(
                                        "username" to userData.value?.username,
                                        "userId" to userId,
                                        "premisesId" to premisesId,
                                        "status" to "pending"
                                    )
                                    requestsRef.add(request)
                                        .addOnSuccessListener { callback.onSuccess() }
                                        .addOnFailureListener { e ->
                                            Log.d(DEBUG, "${e.message}")
                                            callback.onFailure("Ups, coś poszło nie tak")
                                        }
                                } else {
                                    callback.onFailure("Już wysłano prośbę o dołączenie")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.d(DEBUG, "sendJoinRequest: ${e.message}")
                                callback.onFailure("Ups, coś poszło nie tak")
                            }
                    }
                } else {
                    callback.onFailure("Kod nie istnieje")
                }
            }
            .addOnFailureListener { e ->
                Log.d(DEBUG, "sendJoinRequest: ${e.message}")
                callback.onFailure("Ups, coś poszło nie tak")
            }
    }

     fun fetchJoinRequests() {
        val docRef = cloud.collection("requests")
            .whereEqualTo("premisesId", premisesRepository.premises.value!!.premisesId!!)
            .whereEqualTo("status", "pending")

        docRef.get()
            .addOnSuccessListener { querySnapshot ->
                val requests = mutableListOf<Request>()
                for (document in querySnapshot) {
                    val request = document.toObject(Request::class.java)
                    requests.add(request)
                }
                (requestData as MutableLiveData).postValue(requests)
            }
    }

}