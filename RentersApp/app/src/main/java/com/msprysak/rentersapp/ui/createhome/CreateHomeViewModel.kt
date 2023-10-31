package com.msprysak.rentersapp.ui.createhome

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.msprysak.rentersapp.data.RepositorySingleton
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.data.model.User


class CreateHomeViewModel: ViewModel() {


    private val repository = RepositorySingleton.getInstance()

    val userData: MutableLiveData<User> = repository.sharedUserData

    init {
        repository.fetchUserData()
    }
    fun validateLocalName(localName: String): Boolean {
        return localName.isNotBlank()
    }

    fun createHomeClicked(imageURL: String, localAddress: String, localName: String){
        // Utwórz nowy obiekt Premises

        val newPremises = Premises(
            imageUrl = imageURL,
            localAddress = localAddress,
            localName = localName,
            users = mapOf(userData.value?.userId!! to "landlord"),
            creationDate = FieldValue.serverTimestamp(),
            temporaryCode = null
        )

        repository.createNewPremises(newPremises, userData.value!!)

    }



    val DEBUG = "CreateHomeViewModel"


}
