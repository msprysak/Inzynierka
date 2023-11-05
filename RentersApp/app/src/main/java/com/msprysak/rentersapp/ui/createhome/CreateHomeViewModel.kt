package com.msprysak.rentersapp.ui.createhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.msprysak.rentersapp.data.CallBack
import com.msprysak.rentersapp.data.RepositorySingleton
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.data.model.User


class CreateHomeViewModel: ViewModel() {


    private val repository = RepositorySingleton.getInstance()

    private val userData = repository.sharedUserData
    fun validateLocalName(localName: String): Boolean {
        return localName.isNotBlank()
    }

    fun getUserData(): LiveData<User> {
        return repository.getUserData()
    }

    fun createHome(imageURL: String, localAddress: String, localName: String, callback: CallBack) {
        val newPremises = Premises(
            premisesImageUrl = imageURL,
            address = localAddress,
            name = localName,
            users = mapOf(userData.value?.userId!! to "landlord"),
            creationDate = Timestamp.now()
        )

        repository.createNewPremises(newPremises, userData.value!!, callback)
    }
     fun uploadPremisesPhoto(byteArray: ByteArray){
        repository.uploadPremisesPhoto(byteArray)
    }

    // Funkcja do sprawdzania i tworzenia prośby o dołączenie do grupy
    fun sendJoinRequest(code: String, callback: CallBack){
        repository.sendJoinRequest(code, callback)
    }



    val DEBUG = "CreateHomeViewModel"


}
