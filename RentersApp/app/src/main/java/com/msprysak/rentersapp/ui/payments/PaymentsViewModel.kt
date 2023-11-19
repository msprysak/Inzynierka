package com.msprysak.rentersapp.ui.payments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.data.repositories.PremisesRepository

class PaymentsViewModel : ViewModel() {

    private val userRepository = UserRepositoryInstance.getInstance()

    private val premisesRepository = PremisesRepository.getInstance(userRepository.getUserData())


    val usersListData: MutableLiveData<List<User>> = MutableLiveData()

    fun fetchUsers(){
//        premisesRepository.fetchUsers{ usersList ->
//            usersListData.postValue(usersList)
//        }
    }
}