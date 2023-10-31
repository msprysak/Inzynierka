package com.msprysak.rentersapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.data.model.User

class FirebaseRepository {
    private val DEBUG = "FirebaseRepository_DEBUG"

    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cloud = FirebaseFirestore.getInstance()
    val sharedUserData: MutableLiveData<User> = MutableLiveData()
    val sharedPremisesData: MutableLiveData<Premises> = MutableLiveData()
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

    fun getPremisesData(): LiveData<Premises>{
        val docRef = cloud.collection("premises")
            .document(sharedUserData.value?.houseRoles!!["premisesId"]!!)

        docRef.addSnapshotListener{documentSnapshot, e ->
            if (e !=null){
                return@addSnapshotListener
            }
            if (documentSnapshot != null && documentSnapshot.exists()){
                val premises = documentSnapshot.toObject(Premises::class.java)
                sharedPremisesData.value = premises!!
            }
        }
        return sharedPremisesData
    }

    fun getUserData(): LiveData<User> {
        val docRef = cloud.collection("users")
            .document(auth.currentUser!!.uid)

        docRef.addSnapshotListener{documentSnapshot, e ->
            if (e !=null){
                return@addSnapshotListener
            }
            if (documentSnapshot != null && documentSnapshot.exists()){
                val user = documentSnapshot.toObject(User::class.java)
                sharedUserData.value = user!!
            }
        }
        return sharedUserData
    }
    fun fetchUserData(): LiveData<User> {

        val userDocumentReference = cloud.collection("users")
            .document(auth.currentUser!!.uid)

        userDocumentReference.get()
            .addOnSuccessListener { userDocumentSnapshot ->
                val user = userDocumentSnapshot.toObject(User::class.java)
                sharedUserData.value = user!!
            }
            .addOnFailureListener {
                Log.d(DEBUG, "fetchUserData: ${it.message}")
            }

        return sharedUserData
    }

    fun createNewPremises(premises: Premises, user: User) {

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
                    }
                    .addOnFailureListener {
                        Log.d(DEBUG, "createNewPremises: ${it.message}")
                    }

                Log.d(DEBUG, "addPremises: Success")
            }
            .addOnFailureListener {
                Log.d(DEBUG, "addPremises: ${it.message}")
            }
    }

    fun uploadUserPhoto(bytes: ByteArray) {

        storage.getReference("users")
            .child("${auth.currentUser!!.uid}.jpg")
            .putBytes(bytes)
            .addOnCompleteListener() {
            }
            .addOnSuccessListener {
                Log.d(DEBUG, "uploadUserPhoto: Success")
                getPhotoDownloadUrl(it.storage)
            }
            .addOnFailureListener() {
                Log.d(DEBUG, "uploadUserPhoto: ${it.message}")
            }
    }

    private fun getPhotoDownloadUrl(storage: StorageReference) {
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
