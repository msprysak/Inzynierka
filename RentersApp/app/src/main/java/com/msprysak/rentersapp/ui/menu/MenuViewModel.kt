package com.msprysak.rentersapp.ui.menu

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.RepositorySingleton
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.databinding.DialogAddHomeBinding

class MenuViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val repository = RepositorySingleton.getInstance()
    val userData: MutableLiveData<User> = repository.sharedUserData

    fun generateCode(){
        println("generateCode")
    }
}