package com.msprysak.rentersapp.ui.createhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.interfaces.CallBack
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.data.repositories.JoinRequestRepository
import com.msprysak.rentersapp.data.repositories.PremisesRepository


class CreateHomeViewModel: ViewModel() {


//    private val repository = RepositorySingleton.getInstance()

    private val repository = UserRepositoryInstance.getInstance()
    private val userData = repository.user
    private val requestRepository = JoinRequestRepository(userData)
    private val premisesRepository = PremisesRepository.getInstance(userData)
    fun validateLocalName(localName: String): Boolean {
        return localName.isNotBlank()
    }

    fun getUserData(): LiveData<User> {
        return repository.getUserData()
    }

    fun createPremises(imageURL: String, localAddress: String, localName: String, callback: CallBack) {
        val newPremises = Premises(
            premisesImageUrl = imageURL,
            address = localAddress,
            name = localName,
            users = mapOf(userData.value?.userId!! to "landlord"),
            creationDate = Timestamp.now()
        )

        premisesRepository.createNewPremises(newPremises, userData.value!!, callback)
    }
     fun uploadPremisesPhoto(byteArray: ByteArray){
        premisesRepository.uploadPremisesPhoto(byteArray)
    }

    // Funkcja do sprawdzania i tworzenia prośby o dołączenie do grupy
    fun sendJoinRequest(code: String, callback: CallBack){
        requestRepository.sendJoinRequest(code, callback)
    }



    val DEBUG = "CreateHomeViewModel"


}
