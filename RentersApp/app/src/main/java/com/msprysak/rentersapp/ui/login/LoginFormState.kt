package com.msprysak.rentersapp.ui.login

/**
 * Data validation state of the login form.
 */

data class LoginFormState(
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val loginError: String? = null,
    val isDataValid: Boolean = false
)