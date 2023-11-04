package com.msprysak.rentersapp.ui.addusers

import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.RepositorySingleton

class AddUsersViewModel: ViewModel() {

    private val repository = RepositorySingleton.getInstance()

    private val premises = repository.sharedPremisesData


    fun addTemporaryCode(randomCode: String) {
        repository.addTemporaryCode(repository.sharedPremisesData.value!!.premisesId!!, randomCode)
    }

}