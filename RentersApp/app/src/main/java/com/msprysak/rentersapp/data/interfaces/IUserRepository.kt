package com.msprysak.rentersapp.data.interfaces

import androidx.lifecycle.LiveData
import com.msprysak.rentersapp.data.model.User

interface IUserRepository{

    fun uploadUserPhoto(byteArray: ByteArray)
    fun editProfileData(map: Map<String, String>)

    fun createNewUser(user: User)

    fun getUserData(): LiveData<User>
    fun fetchUserData()
    fun updatePassword(password: String)

}