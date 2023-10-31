package com.msprysak.rentersapp.ui.createhome

data class CreateHomeFormState (
    val localNameError: Int? = null,
    val localAddressError: Int? = null,
    val createHomeError: String? = null,
    val isDataValid: Boolean = false,
)