package com.msprysak.rentersapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.msprysak.rentersapp.CallBack
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.data.model.Request
import com.msprysak.rentersapp.data.model.User

class FirebaseRepository {
    private val DEBUG = "FirebaseRepository_DEBUG"

    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cloud = FirebaseFirestore.getInstance()
    val sharedPremisesData: MutableLiveData<Premises> = MutableLiveData()
    val sharedUserData: MutableLiveData<User> = MutableLiveData()
    private val joinRequests: MutableLiveData<List<Request>> = MutableLiveData()

    fun acceptJoinRequest(request: Request, callback: CallBack) {
        val requestCollectionRef = cloud.collection("requests")
            .whereEqualTo("premisesId", sharedPremisesData.value!!.premisesId)
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
                        val users = premisesData.get("users") as MutableMap<String, String>
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
                            println("Transaction failure: ${e.message}")
                            callback.onFailure("Ups, coś poszło nie tak")
                        }
                } else {
                    callback.onFailure("Brak pasujących żądań")
                }
            }
            .addOnFailureListener { e ->
                println("acceptJoinRequest: ${e.message}")
                callback.onFailure("Ups, coś poszło nie tak")
            }
    }







    fun rejectJoinRequest(request: Request, callback: CallBack) {
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

    fun activateJoinRequestsListener(): LiveData<List<Request>> {
        val houseRoles = sharedUserData.value?.houseRoles
        if (!houseRoles.isNullOrEmpty()) {
            val premisesId = houseRoles.keys.first()
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
                        println(request)
                    }
                    joinRequests.postValue(requests)
                }
            }
        }
        return joinRequests
    }

    fun addTemporaryCode(randomCode: String) {
        val data = hashMapOf(
            "code" to randomCode,
            "premisesId" to (sharedUserData.value?.houseRoles?.keys?.first() ?: "null"),
            "creationTime" to FieldValue.serverTimestamp()
        )

        val docRef = cloud.collection("temporaryCodes")

        docRef.whereEqualTo("premisesId", sharedUserData.value?.houseRoles?.keys?.first() ?: "null")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    docRef.document(randomCode)
                        .set(data)
                        .addOnSuccessListener {
                            Log.d(DEBUG, "Dodano nowy dokument")
                        }
                        .addOnFailureListener { e ->
                            Log.d(DEBUG, "Błąd podczas dodawania nowego dokumentu: ${e.message}")
                        }
                } else {
                    docRef.document(querySnapshot.documents[0].id)
                        .delete()
                    docRef.document(randomCode)
                        .set(data)
                    Log.d(DEBUG, "Dokument o premisesId  już istnieje.")
                }
            }
            .addOnFailureListener { e ->
                Log.d(DEBUG, "Błąd podczas sprawdzania dokumentów: ${e.message}")
            }

    }

    fun updatePassword(password: String){
        auth.currentUser!!.updatePassword(password)
            .addOnSuccessListener {
                Log.d(DEBUG, "updatePassword: Success")
            }
            .addOnFailureListener {
                Log.d(DEBUG, "updatePassword: ${it.message}")
            }
    }

    fun sendJoinRequest(code: String, callback: CallBack) {
        val userId = auth.currentUser?.uid
        val temporaryCodesRef = cloud.collection("temporaryCodes")

        temporaryCodesRef.whereEqualTo("code", code)
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
                                        "username" to sharedUserData.value?.username,
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


//    private fun checkIfCodeExists(randomCode: String, callBack: (Boolean) -> Unit) {
//        val docRef = cloud.collection("temporaryCodes")
//            .whereEqualTo("code", randomCode)
//            .get()
//            .addOnSuccessListener { documents ->
//                if (documents.isEmpty) {
//
//                }
//            }
//    }

    private fun checkIfUserAlreadyCreated(premisesId: String) {
        cloud.collection("temporaryCodes")
            .whereEqualTo("premisesId", premisesId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    // Usuń znaleziony dokument
                    document.reference.delete()
                    Log.d(DEBUG, "Dokument został usunięty: ${document.id}")
                }
            }
            .addOnFailureListener { e ->
                Log.d(DEBUG, "${e.message}")
            }
    }

    fun getUserData(): LiveData<User> {
        val docRef = cloud.collection("users")
            .document(auth.currentUser!!.uid)

        docRef.addSnapshotListener { documentSnapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                sharedUserData.postValue(user!!)
            }
        }
        return sharedUserData
    }
    fun getPremisesData(): LiveData<Premises> {
        val docRef = cloud.collection("premises")
            .document(sharedUserData.value?.houseRoles?.keys?.first()!!)

        docRef.addSnapshotListener { documentSnapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                val premises = documentSnapshot.toObject(Premises::class.java)
                sharedPremisesData.postValue(premises!!)
            }
        }

        return sharedPremisesData
    }

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

    fun createNewUser(user: User) {
        cloud.collection("users")
            .document(user.userId!!)
            .set(user)
    }


    fun editProfileData(map: Map<String, String>) {
        cloud.collection("users")
            .document(auth.currentUser!!.uid)
            .update(map)
            .addOnSuccessListener {
                Log.d(DEBUG, "editProfileData: Success")
            }
            .addOnFailureListener {
                Log.d(DEBUG, "editProfileData: ${it.message}")
            }
    }

    fun editPremisesData(map: Map<String, String>) {
        cloud.collection("premises")
            .document(sharedUserData.value?.houseRoles?.keys?.first()!!)
            .update(map)
            .addOnSuccessListener { Log.d(DEBUG, "editPremisesData: Success") }
            .addOnFailureListener {
                Log.d(DEBUG, "editPremisesData: ${it.message}")
            }
    }






    fun createNewPremises(premises: Premises, user: User, callback: CallBack) {
        cloud.collection("premises")
            .add(premises)
            .addOnSuccessListener { premisesDocumentReference ->
                val premisesId = premisesDocumentReference.id

                val userRoleInNewHouse = mapOf(premisesId to "landlord")

                val userDocumentReference = cloud.collection("users")
                    .document(user.userId!!)

                userDocumentReference.update("houseRoles", userRoleInNewHouse)
                    .addOnSuccessListener {
                        Log.d(DEBUG, "createNewPremises: Success")
                        callback.onSuccess()
                    }
                    .addOnFailureListener { e ->
                        val errorMessage = "createNewPremises: ${e.message}"
                        callback.onFailure(errorMessage)
                    }

                Log.d(DEBUG, "addPremises: Success")
            }
            .addOnFailureListener { e ->
                val errorMessage = "addPremises: ${e.message}"
                Log.d(DEBUG, errorMessage)
                callback.onFailure(errorMessage)
            }
    }


    fun uploadUserPhoto(bytes: ByteArray) {

        storage.getReference("users")
            .child("${auth.currentUser!!.uid}.jpg")
            .putBytes(bytes)
            .addOnCompleteListener {
            }
            .addOnSuccessListener {
                Log.d(DEBUG, "uploadUserPhoto: Success")
                getUserPhotoDownloadUrl(it.storage)
            }
            .addOnFailureListener {
                Log.d(DEBUG, "uploadUserPhoto: ${it.message}")
            }
    }

    fun uploadPremisesPhoto(bytes: ByteArray) {
        storage.getReference("premises")
            .child("${sharedUserData.value?.houseRoles?.keys?.first()}.jpg")
            .putBytes(bytes)
            .addOnCompleteListener {

            }
            .addOnSuccessListener {
                Log.d(DEBUG, "uploadPremisesPhoto: Success")
                getPremisesPhotoDownloadUrl(it.storage)
            }
            .addOnFailureListener {
                Log.d(DEBUG, "uploadPremisesPhoto: ${it.message}")
            }
    }

    private fun getPremisesPhotoDownloadUrl(storage: StorageReference) {
        storage.downloadUrl
            .addOnSuccessListener { updatePremisesPhoto(it.toString()) }
            .addOnFailureListener {
                Log.d(DEBUG, "getPhotoDownloadUrl: ${it.message}")
            }
    }

    private fun updatePremisesPhoto(url: String?) {
        cloud.collection("premises")
            .document(sharedUserData.value?.houseRoles?.keys?.first()!!)
            .update("premisesImageUrl", url)
            .addOnSuccessListener {
                Log.d(DEBUG, "updatePremisesPhoto: Success")
            }
            .addOnFailureListener {
                Log.d(DEBUG, "updatePremisesPhoto: ${it.message.toString()}")
            }

    }


    private fun getUserPhotoDownloadUrl(storage: StorageReference) {
        storage.downloadUrl
            .addOnSuccessListener { updateUserPhoto(it.toString()) }
            .addOnFailureListener {
                Log.d(DEBUG, "getPhotoDownloadUrl: ${it.message}")
            }
    }

    private fun updateUserPhoto(url: String?) {
        cloud.collection("users")
            .document(auth.currentUser!!.uid)
            .update("profilePictureUrl", url)
            .addOnSuccessListener {
                Log.d(DEBUG, "updateUserPhoto: Success")
            }
            .addOnFailureListener {
                Log.d(DEBUG, "updateUserPhoto: ${it.message.toString()}")
            }

    }


}
