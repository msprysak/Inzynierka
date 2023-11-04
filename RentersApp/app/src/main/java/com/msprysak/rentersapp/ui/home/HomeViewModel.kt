package com.msprysak.rentersapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.RepositorySingleton
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.data.model.User

class HomeViewModel : ViewModel() {

    private val repository = RepositorySingleton.getInstance()

   fun getPremisesData(): LiveData<Premises> {
        return repository.sharedPremisesData
    }

    fun uploadPremisesPhoto(byteArray: ByteArray){
        repository.uploadPremisesPhoto(byteArray)
    }


    fun editPermisesData(map: Map<String, String>) {
        repository.editPremisesData(map)
    }



}