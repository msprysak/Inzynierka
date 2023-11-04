package com.msprysak.rentersapp.ui.createhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.msprysak.rentersapp.data.CreateHomeCallback
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

    fun createHome(imageURL: String, localAddress: String, localName: String, callback: CreateHomeCallback) {
        val newPremises = Premises(
            premisesImageUrl = imageURL,
            address = localAddress,
            name = localName,
            users = mapOf(userData.value?.userId!! to "landlord"),
            creationDate = Timestamp.now()
        )

        repository.createNewPremises(newPremises, userData.value!!, callback)
    }




    val DEBUG = "CreateHomeViewModel"


}
