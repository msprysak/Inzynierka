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
    val sharedPremisesData: MutableLiveData<Premises> = MutableLiveData()

    val sharedUserData: MutableLiveData<User> = MutableLiveData()

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
    fun getPremisesData(premisesId: String): LiveData<Premises> {
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
