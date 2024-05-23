package com.msprysak.rentersapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.data.repositories.PremisesRepository

class HomeViewModel : ViewModel() {

    private val userInstance = UserRepositoryInstance.getInstance()
    private val repository = PremisesRepository.getInstance(userInstance.getUserData())

    fun getUserData(): User {
        return userInstance.user.value!!
    }

   fun getPremisesData(): LiveData<Premises> {
        return repository.getPremisesData()
    }

    fun uploadPremisesPhoto(byteArray: ByteArray){
        repository.uploadPremisesPhoto(byteArray)
    }


    fun editPermisesData(map: Map<String, String>) {
        repository.editPremisesData(map)
    }



}