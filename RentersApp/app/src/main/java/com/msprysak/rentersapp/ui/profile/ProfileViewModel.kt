package com.msprysak.rentersapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.RepositorySingleton
import com.msprysak.rentersapp.data.model.User
import kotlinx.coroutines.yield

class ProfileViewModel: ViewModel() {

    val repository = RepositorySingleton.getInstance()

    fun editProfileData(map: Map<String, String>){
        repository.editProfileData(map)
    }

    fun getUserData(): LiveData<User> {
        return repository.getUserData()
    }
    fun uploadUserPhoto(byteArray: ByteArray){
        repository.uploadUserPhoto(byteArray)
    }

    fun updatePassword(password: String){
        repository.updatePassword(password)
    }


}