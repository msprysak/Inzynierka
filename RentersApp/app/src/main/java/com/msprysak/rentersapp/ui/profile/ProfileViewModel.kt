package com.msprysak.rentersapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.msprysak.rentersapp.data.RepositorySingleton
import com.msprysak.rentersapp.data.model.User

class ProfileViewModel: ViewModel() {

    val repository = RepositorySingleton.getInstance()
    private val fbAuth = FirebaseAuth.getInstance()

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

    fun signOut(){
        fbAuth.signOut()
    }
}