package com.msprysak.rentersapp.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.data.repositories.PremisesRepository

class UsersViewModel: ViewModel() {

    val userRepository = UserRepositoryInstance.getInstance()

    val premisesRepository = PremisesRepository.getInstance(userRepository.user)

    fun fetchUsers(): LiveData<List<User>> {
        val premises = premisesRepository.premises.value
        if (premises?.users != null) {
            return premisesRepository.getUsersByIds(premisesRepository.premises.value!!.users!!.keys.toList())
        } else {
            return MutableLiveData<List<User>>().apply { value = emptyList() }
        }
    }

    fun deleteUser(user: User) {
        //TODO
    }

}