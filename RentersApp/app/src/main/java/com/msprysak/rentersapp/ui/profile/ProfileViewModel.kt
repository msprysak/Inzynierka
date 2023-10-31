package com.msprysak.rentersapp.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.RepositorySingleton
import com.msprysak.rentersapp.data.model.User
import kotlinx.coroutines.yield

class ProfileViewModel: ViewModel() {

    val repository = RepositorySingleton.getInstance()

    val userData: MutableLiveData<User> = repository.sharedUserData

    fun editProfileData(map: Map<String, String>){
        repository.editProfileData(map)
    }

    fun uploadUserPhoto(byteArray: ByteArray){
        repository.uploadUserPhoto(byteArray)
    }

}