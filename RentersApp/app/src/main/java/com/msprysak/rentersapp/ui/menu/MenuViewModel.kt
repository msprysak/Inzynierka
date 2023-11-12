package com.msprysak.rentersapp.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.model.User

class MenuViewModel : ViewModel() {

    private val repository = UserRepositoryInstance.getInstance()
    fun getUserData(): LiveData<User>{
        return repository.getUserData()
    }
}