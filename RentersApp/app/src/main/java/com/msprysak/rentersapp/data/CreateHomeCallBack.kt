package com.msprysak.rentersapp.data

interface CreateHomeCallback {
    fun onSuccess()
    fun onFailure(errorMessage: String)
}
