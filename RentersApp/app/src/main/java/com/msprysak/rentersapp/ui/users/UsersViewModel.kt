package com.msprysak.rentersapp.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.data.repositories.PremisesRepository

class UsersViewModel: ViewModel() {

    val userRepository = UserRepositoryInstance.getInstance()

    private val premisesRepository = PremisesRepository.getInstance(userRepository.user)

    fun fetchUsers(): LiveData<List<User>> {
        // Sprawdź, czy premisesRepository.premises i premisesRepository.premises.value nie są null
        val premises = premisesRepository.premises.value
        println("premises $premises")
        if (premises != null && premises.users != null) {
            val userIdsList = premises.users.keys.toList()
            return premisesRepository.getUsersByIds(userIdsList)
        } else {
            // Zwróć pustą listę lub inny odpowiedni wynik w przypadku null
            return MutableLiveData<List<User>>().apply { value = emptyList() }
        }
    }

    fun deleteUser(user: User) {
        println("deleteUser: $user")
//        premisesRepository.deleteUserFromPremises(user)
    }

}