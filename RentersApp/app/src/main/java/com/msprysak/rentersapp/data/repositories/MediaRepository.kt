package com.msprysak.rentersapp.data.repositories

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.interfaces.CallBack
import com.msprysak.rentersapp.data.model.Media
import java.util.Date
import java.util.UUID

class MediaRepository {

    private val DEBUG = "MediaRepository_DEBUG"

    private val storage = FirebaseStorage.getInstance()
    private val cloud = FirebaseFirestore.getInstance()

    private val userInstance = UserRepositoryInstance.getInstance()
    private val premisesInstance = PremisesRepository.getInstance(userInstance.getUserData())
    private val currentUserId = userInstance.getUserData().value!!.userId


    fun setupMediaListener(callBack: (List<Media>) -> Unit){

        cloud.collection("media")
            .whereEqualTo("premisesId", premisesInstance.premises.value!!.premisesId!!)
            .orderBy("creationDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(DEBUG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val media = mutableListOf<Media>()

                    for (doc in snapshot) {
                        val mediaItem = doc.toObject(Media::class.java)
                        media.add(mediaItem)
                        Log.d(DEBUG, "Current data: $mediaItem")
                    }
                    callBack(media)
                    Log.d(DEBUG, "Current data: $media")


                } else {
                    Log.d(DEBUG, "Current data: null")
                }
            }
    }

    fun createNewMedia(
        media: Media,
        selectedImages: List<Uri>,
        callBack: CallBack
    ) {
        val mediaDocRef = cloud.collection("media").document()

        val mediaData = hashMapOf(
            "mediaId" to mediaDocRef.id,
            "userId" to currentUserId,
            "premisesId" to premisesInstance.premises.value!!.premisesId!!,
            "mediaTitle" to media.mediaTitle,
            "mediaDate" to media.mediaDate,
            "mediaImages" to listOf<String>(),
            "creationDate" to Date(System.currentTimeMillis()),
            )

        val uploadedPhotoUrls = mutableListOf<String>()

        if (selectedImages.isNotEmpty()) {
            selectedImages.forEach { selectedImage ->
                val randomFileName = UUID.randomUUID().toString()
                val storageRef = storage.reference
                    .child("premises")
                    .child(premisesInstance.premises.value!!.premisesId!!)
                    .child("media")
                    .child(mediaDocRef.id)
                    .child(randomFileName)

                storageRef.putFile(selectedImage)
                    .continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        storageRef.downloadUrl
                    }
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val downloadUrl = task.result.toString()
                            uploadedPhotoUrls.add(downloadUrl)

                            if (uploadedPhotoUrls.size == selectedImages.size) {
                                val finalPhotos = uploadedPhotoUrls.toList()

                                mediaData["mediaImages"] = finalPhotos

                                mediaDocRef.set(mediaData)
                                    .addOnSuccessListener {
                                        //Log.d(DEBUG, "DocumentSnapshot successfully written!")
                                        callBack.onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(DEBUG, "Error writing document", e)
                                        callBack.onFailure("Ups, coś poszło nie tak, spróbuj ponownie później.")
                                    }
                            }
                        }
                    }
            }
        } else {
            mediaDocRef.set(mediaData)
                .addOnSuccessListener {
                    Log.d(DEBUG, "DocumentSnapshot successfully written!")
                    callBack.onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.w(DEBUG, "Error writing document", e)
                    callBack.onFailure("Ups, coś poszło nie tak, spróbuj ponownie później.")
                }
        }
    }

}
