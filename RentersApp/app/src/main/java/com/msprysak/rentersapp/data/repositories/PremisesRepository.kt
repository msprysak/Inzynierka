package com.msprysak.rentersapp.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.msprysak.rentersapp.data.interfaces.CallBack
import com.msprysak.rentersapp.data.interfaces.IPremisesRepository
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.data.model.User

class PremisesRepository private  constructor(private val userData: LiveData<User>) :
    IPremisesRepository {

    private val DEBUG = "PremisesRepository_DEBUG"

    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cloud = FirebaseFirestore.getInstance()

    val usersListData: LiveData<List<User>> = MutableLiveData()
    val premises: LiveData<Premises>
        get() = premisesLiveData

    private val premisesLiveData: MutableLiveData<Premises> = MutableLiveData()

    companion object {
        // Jedyna instancja PremisesRepository
        @Volatile
        private var instance: PremisesRepository? = null

        // Metoda do uzyskania instancji PremisesRepository
        fun getInstance(userData: LiveData<User>): PremisesRepository {
            return instance ?: synchronized(this) {
                instance ?: PremisesRepository(userData).also { instance = it }
            }
        }
    }

    fun getCurrentPremisesId(): String {
        return userData.value?.houseRoles?.keys?.first() ?: "null"
    }

    override fun createNewPremises(premises: Premises, user: User, callback: CallBack) {
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

    override fun editPremisesData(map: Map<String, String>) {
        cloud.collection("premises")
            .document(userData.value?.houseRoles?.keys?.first()!!)
            .update(map)
            .addOnSuccessListener { Log.d(DEBUG, "editPremisesData: Success") }
            .addOnFailureListener {
                Log.d(DEBUG, "editPremisesData: ${it.message}")
            }
    }

    override fun getPremisesData(): LiveData<Premises> {
        val docRef = cloud.collection("premises")
            .document(userData.value?.houseRoles?.keys?.first()!!)

        docRef.addSnapshotListener { documentSnapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                val premises = documentSnapshot.toObject(Premises::class.java)
                premisesLiveData.postValue(premises!!)
            }
        }
        return premisesLiveData
    }

    override fun addTemporaryCode(randomCode: String) {
        val data = hashMapOf(
            "code" to randomCode,
            "premisesId" to (userData.value?.houseRoles?.keys?.first() ?: "null"),
            "creationTime" to FieldValue.serverTimestamp()
        )

        val docRef = cloud.collection("temporaryCodes")

        docRef.whereEqualTo("premisesId", userData.value?.houseRoles?.keys?.first() ?: "null")
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

    override fun fetchPremisesData(): LiveData<Premises> {
        cloud.collection("premises")
            .document(userData.value?.houseRoles?.keys?.first()!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val premisesData = documentSnapshot.toObject(Premises::class.java)
                premisesLiveData.postValue(premisesData!!)

                Log.d(DEBUG, "fetchPremisesData: Success")
            }
            .addOnFailureListener {
                Log.d(DEBUG, "fetchPremisesData: ${it.message}")
            }
        return premisesLiveData
    }

    override fun getUsersByIds(ids: List<String>): LiveData<List<User>> {
        val docRef = cloud.collection("users")
            .whereIn("userId", ids)

        docRef.addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                Log.d(DEBUG, "getUsersDataById: ${e.message}")
                return@addSnapshotListener
            }

            val usersList = mutableListOf<User>()
            for (document in querySnapshot!!) {
                val user = document.toObject(User::class.java)
                usersList.add(user)
            }


            (usersListData as MutableLiveData).postValue(usersList)
            Log.d(DEBUG, "getUsersDataById: Success")
        }
        return usersListData
    }

    override fun uploadPremisesPhoto(bytes: ByteArray) {
        storage.getReference("premises")
            .child(userData.value?.houseRoles?.keys?.first()!!)
            .child("${userData.value?.houseRoles?.keys?.first()}.jpg")
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

    override fun deleteUserFromPremises(users: User) {
        TODO("Not yet implemented")
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
            .document(userData.value!!.houseRoles!!.keys.first())
            .update("premisesImageUrl", url)
            .addOnSuccessListener {
                Log.d(DEBUG, "updatePremisesPhoto: Success")
            }
            .addOnFailureListener {
                Log.d(DEBUG, "updatePremisesPhoto: ${it.message.toString()}")
            }

    }

}