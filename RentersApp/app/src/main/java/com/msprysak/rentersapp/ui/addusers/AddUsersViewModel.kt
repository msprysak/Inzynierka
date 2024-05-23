package com.msprysak.rentersapp.ui.addusers

import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.repositories.PremisesRepository

class AddUsersViewModel: ViewModel() {

    private val repository = UserRepositoryInstance.getInstance()

    private val premisesRepository = PremisesRepository.getInstance(repository.getUserData())

    fun addTemporaryCode(randomCode: String) {

        return premisesRepository.addTemporaryCode(randomCode)
    }

}