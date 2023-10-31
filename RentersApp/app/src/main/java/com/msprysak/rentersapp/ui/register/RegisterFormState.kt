package com.msprysak.rentersapp.ui.register

data class RegisterFormState (
    val usernameError : Int? = null,
    val emailAddressError: Int? = null,
    val passwordError: Int? = null,
    val confirmPasswordError: Int? = null,
    val isDataValid: Boolean = false,
    val signupError: String? = null,
    val signupSuccess: Boolean = false
)