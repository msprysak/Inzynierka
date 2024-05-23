package com.msprysak.rentersapp.ui.addpremises

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.data.repositories.PremisesRepository

class AddPremisesViewModel: ViewModel() {

    val premisesRepository = PremisesRepository.getInstance(UserRepositoryInstance.getInstance().user)

    private val _premisesList: MutableLiveData<List<Premises>> = MutableLiveData()

    val premisesList: LiveData<List<Premises>> get() = _premisesList

    fun getAllPremises() {
        premisesRepository.getAllPremises{
            _premisesList.postValue(it)
        }
    }


}