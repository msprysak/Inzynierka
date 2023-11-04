package com.msprysak.rentersapp.ui.addusers

import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.RepositorySingleton

class AddUsersViewModel: ViewModel() {

    private val repository = RepositorySingleton.getInstance()

    fun addTemporaryCode(randomCode: String) {

        return repository.addTemporaryCode(randomCode)
    }

}