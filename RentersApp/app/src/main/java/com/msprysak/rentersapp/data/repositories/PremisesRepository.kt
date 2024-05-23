package com.msprysak.rentersapp.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.interfaces.CallBack
import java.time.LocalDate

class PremisesRepository private  constructor(private val userData: LiveData<User>) {

    private val DEBUG = "PremisesRepository_DEBUG"

    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cloud = FirebaseFirestore.getInstance()

    val usersListData: LiveData<List<User>> = MutableLiveData()

    var premises: LiveData<Premises>
        get() = _premisesLiveData
        set(value) {
            _premisesLiveData.value = value.value
        }


    private var _premisesLiveData: MutableLiveData<Premises> = MutableLiveData()

    fun updatePremises(premises: Premises) {
        _premisesLiveData.value = premises
        Log.d(DEBUG, "Premises updated: ${premises.name}")
    }
    companion object {
        @Volatile
        private var instance: PremisesRepository? = null

        fun getInstance(userData: LiveData<User>): PremisesRepository {
            return instance ?: synchronized(this) {
                instance ?: PremisesRepository(userData).also { instance = it }
            }
        }
    }



    fun getAllPremises(callback: (List<Premises>) -> Unit) {
        val docRef = cloud.collection("premises")
            .whereIn("premisesId", userData.value?.houseRoles?.keys?.toList()!!)

        docRef.addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                Log.d(DEBUG, "getAllPremises: ${e.message}")
                return@addSnapshotListener
            }

            val premisesList = mutableListOf<Premises>()
            for (document in querySnapshot!!) {
                val premises = document.toObject(Premises::class.java)
                premisesList.add(premises)
            }

            callback(premisesList)
            Log.d(DEBUG, "getAllPremises: Success")
        }
    }


    fun createNewPremises(premises: Premises, user: User, callback: CallBack) {
        cloud.collection("premises")
            .add(premises)
            .addOnSuccessListener { premisesDocumentReference ->
                val premisesId = premisesDocumentReference.id

                premisesDocumentReference.update("premisesId", premisesId)
                val userRoleInNewHouse = mapOf(premisesId to "landlord")

                val userDocumentReference = cloud.collection("users")
                    .document(user.userId!!)

                userDocumentReference.set(mapOf("houseRoles" to userRoleInNewHouse), SetOptions.merge())
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

     fun editPremisesData(map: Map<String, String>) {
        cloud.collection("premises")
            .document(premises.value!!.premisesId!!)
            .update(map)
            .addOnSuccessListener { Log.d(DEBUG, "editPremisesData: Success") }
            .addOnFailureListener {
                Log.d(DEBUG, "editPremisesData: ${it.message}")
            }
    }


     fun getPremisesData(): LiveData<Premises> {
        val docRef = cloud.collection("premises")
            .document(if (premises.value?.premisesId != null) premises.value!!.premisesId!! else userData.value!!.houseRoles!!.keys.first())

        docRef.addSnapshotListener { documentSnapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                val premises = documentSnapshot.toObject(Premises::class.java)
                _premisesLiveData.postValue(premises!!)
            }
        }
        return _premisesLiveData
    }
     fun addTemporaryCode(randomCode: String) {
        val data = hashMapOf(
            "code" to randomCode,
            "premisesId" to (premises.value!!.premisesId ?: "null"),
            "creationTime" to FieldValue.serverTimestamp()
        )

        val docRef = cloud.collection("temporaryCodes")

        docRef.whereEqualTo("premisesId", premises.value!!.premisesId ?: "null")
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


     fun getUsersByIds(ids: List<String>): LiveData<List<User>> {
        val usersListData = MutableLiveData<List<User>>()

        val docRef = cloud.collection("users")
            .whereIn("userId", ids)

        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val querySnapshot = task.result
                val usersList = mutableListOf<User>()

                for (document in querySnapshot!!) {
                    val user = document.toObject(User::class.java)
                    usersList.add(user)
                }

                usersListData.value = usersList
                Log.d(DEBUG, "getUsersDataById: Success")
            } else {
                Log.d(DEBUG, "getUsersDataById: ${task.exception?.message}")
            }
        }

        return usersListData
    }


     fun fetchUsers(callback: (List<User>) -> Unit) {

        val docRef = cloud.collection("users")
            .whereIn("userId", premises.value!!.users!!.keys.toList())

        docRef.addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                Log.d(DEBUG, "fetchUsers: ${e.message}")
                return@addSnapshotListener
            }

            val usersList = mutableListOf<User>()
            for (document in querySnapshot!!) {
                val user = document.toObject(User::class.java)
                usersList.add(user)
            }

            callback(usersList)
            Log.d(DEBUG, "fetchUsers: Success")
        }
    }



     fun uploadPremisesPhoto(bytes: ByteArray) {
        storage.getReference("premises")
            .child(premises.value!!.premisesId!!)
            .child("${premises.value!!.premisesId}.jpg")
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

     fun deleteUserFromPremises(users: User) {
        TODO("Not yet implemented")
    }

    fun addTask(title: String, date: LocalDate) {

        val taskData = mapOf("title" to title, "date" to date.atStartOfDay())
        cloud.collection("calendarTasks")
            .document(premises.value!!.premisesId!!)
            .collection("calendarTask")
            .document()
            .set(taskData)
    }

    fun getCalendarTasks(){
        cloud.collection("calendarTasks")
            .document(premises.value!!.premisesId!!)
            .collection("tasks")
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                for (document in querySnapshot!!) {
                    Log.d(DEBUG, "getCalendarTasks: ${document.data}")
                }
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
            .document(premises.value!!.premisesId!!)
            .update("premisesImageUrl", url)
            .addOnSuccessListener {
                Log.d(DEBUG, "updatePremisesPhoto: Success")
            }
            .addOnFailureListener {
                Log.d(DEBUG, "updatePremisesPhoto: ${it.message.toString()}")
            }

    }

}