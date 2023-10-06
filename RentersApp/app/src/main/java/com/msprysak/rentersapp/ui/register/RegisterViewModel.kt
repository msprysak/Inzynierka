package com.msprysak.rentersapp.ui.register

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.LoginRepository
import com.msprysak.rentersapp.data.Result
import org.w3c.dom.Text
import java.sql.Struct

class RegisterViewModel : ViewModel() {

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState : MutableLiveData<RegisterFormState> = _registerForm

    private val fbAuth = FirebaseAuth.getInstance()
    private val LOG = "RegisterViewModel"

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$").matches(password)
    }
    private fun isConfirmPasswordValid(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun registerDataChanged(email: String, password: String, confirmPassword: String){
        if (!isEmailValid(email)){
            _registerForm.value = RegisterFormState(emailAddressError = R.string.invalid_email)
        } else if (!isPasswordValid(password)){
            _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else if (!isConfirmPasswordValid(password, confirmPassword)){
            _registerForm.value = RegisterFormState(confirmPasswordError = R.string.invalid_confirm_password)
        } else {
            _registerForm.value = RegisterFormState(isDataValid = true)
        }
    }



}