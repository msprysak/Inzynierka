package com.msprysak.rentersapp.ui

import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.RepositorySingleton

class SharedViewModel: ViewModel() {

    private val repository = RepositorySingleton.getInstance()

}