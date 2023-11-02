package com.msprysak.rentersapp.ui.createhome

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.msprysak.rentersapp.data.RepositorySingleton
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.data.model.User


class CreateHomeViewModel: ViewModel() {


    private val repository = RepositorySingleton.getInstance()

    val userData: MutableLiveData<User> = repository.sharedUserData

    fun validateLocalName(localName: String): Boolean {
        return localName.isNotBlank()
    }

    fun createHomeClicked(imageURL: String, localAddress: String, localName: String){
        // Utwórz nowy obiekt Premises

        val newPremises = Premises(
            premisesImageUrl = imageURL,
            address = localAddress,
            name = localName,
            users = mapOf(userData.value?.userId!! to "landlord"),
            creationDate = Timestamp.now()
        )

        repository.createNewPremises(newPremises, userData.value!!)

    }



    val DEBUG = "CreateHomeViewModel"


}
