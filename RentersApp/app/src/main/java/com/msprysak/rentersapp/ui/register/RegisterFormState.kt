package com.msprysak.rentersapp.ui.register

data class RegisterFormState (
    val emailAddressError: Int? = null,
    val passwordError: Int? = null,
    val confirmPasswordError: Int? = null,
    val isDataValid: Boolean = false
)