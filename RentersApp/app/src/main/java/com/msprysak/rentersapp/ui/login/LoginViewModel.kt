package com.msprysak.rentersapp.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.R


class LoginViewModel : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm


    fun loginDataChanged(username: String) {

        if (!isEmailValid(username)) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_email)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }


    // A placeholder username validation check
    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


}