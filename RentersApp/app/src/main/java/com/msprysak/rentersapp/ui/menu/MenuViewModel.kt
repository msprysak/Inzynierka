package com.msprysak.rentersapp.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.RepositorySingleton
import com.msprysak.rentersapp.data.model.User

class MenuViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val repository = RepositorySingleton.getInstance()
    val userData: MutableLiveData<User> = repository.sharedUserData

    fun getUserData(): LiveData<User>{
        return repository.getUserData()
    }
    fun generateCode(){
        println("generateCode")
    }
}