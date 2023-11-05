package com.msprysak.rentersapp.data

interface CallBack {
    fun onSuccess()
    fun onFailure(errorMessage: String)
}
