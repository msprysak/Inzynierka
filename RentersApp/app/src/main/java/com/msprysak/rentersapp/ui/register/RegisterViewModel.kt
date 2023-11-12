package com.msprysak.rentersapp.ui.register

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.model.User

class RegisterViewModel : ViewModel() {

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState : MutableLiveData<RegisterFormState> = _registerForm

    private val userRepository = UserRepositoryInstance.getInstance()


    private fun createNewUser(user: User){
        userRepository.createNewUser(user)
    }

    private var auth= FirebaseAuth.getInstance()
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

    private fun isUsernameValid(username: String): Boolean {
        return username.length >= 3 && Regex("^[a-zA-Z0-9]+$").matches(username)
    }

    fun registerDataChanged(username: String, email: String, password: String, confirmPassword: String){
        if (!isUsernameValid(username)) {
            _registerForm.value = RegisterFormState(usernameError  = R.string.invalid_username)
        } else if (!isEmailValid(email)){
            _registerForm.value = RegisterFormState(emailAddressError = R.string.invalid_email)
        } else if (!isPasswordValid(password)){
            _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else if (!isConfirmPasswordValid(password, confirmPassword)){
            _registerForm.value = RegisterFormState(confirmPasswordError = R.string.invalid_confirm_password)
        } else {
            _registerForm.value = RegisterFormState(isDataValid = true)
        }
    }
    fun signupClicked(username: String, email: String, password: String){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener { authResult ->
                if (authResult.user != null) {

                    val user = User(
                        userId = authResult.user!!.uid,
                        username = username,
                        email = authResult.user!!.email,
                        phoneNumber = "",
                        profilePictureUrl = "",
                        houseRoles = null


                    )
                    createNewUser(user)
                    _registerForm.value = RegisterFormState(signupSuccess = true)
                }

            }
            .addOnFailureListener{exc ->
                _registerForm.value = RegisterFormState(signupError = exc.message)
                Log.d(LOG, "Failed to create user: ${exc.message}")
            }
    }


}