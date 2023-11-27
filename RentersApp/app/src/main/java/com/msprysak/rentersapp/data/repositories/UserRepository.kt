package com.msprysak.rentersapp.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.msprysak.rentersapp.data.model.User

class UserRepository {

    private val DEBUG = "UserRepository_DEBUG"


    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cloud = FirebaseFirestore.getInstance()
    val user: LiveData<User> = MutableLiveData()


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
    private fun getUserPhotoDownloadUrl(storage: StorageReference) {
        storage.downloadUrl
            .addOnSuccessListener { updateUserPhoto(it.toString()) }
            .addOnFailureListener {
                Log.d(DEBUG, "getPhotoDownloadUrl: ${it.message}")
            }
    }

     fun uploadUserPhoto(byteArray: ByteArray) {
        storage.getReference("users")
            .child("${auth.currentUser!!.uid}.jpg")
            .putBytes(byteArray)
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

     fun createNewUser(user: User) {
        cloud.collection("users")
            .document(user.userId!!)
            .set(user)
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
                (this.user as MutableLiveData).postValue(user)
            }
        }
        return user
    }

     fun fetchUserData() {
        val docRef = cloud.collection("users")
            .document(auth.currentUser!!.uid)

        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    (this.user as MutableLiveData).postValue(user)
                } else {
                    Log.d(DEBUG, "fetchUserData: Document doesn't exist")
                }
            }
    }


     fun updatePassword(password: String) {
        auth.currentUser!!.updatePassword(password)
            .addOnSuccessListener {
                Log.d(DEBUG, "updatePassword: Success")
            }
            .addOnFailureListener {
                Log.d(DEBUG, "updatePassword: ${it.message}")
            }
    }



}


