package com.msprysak.rentersapp.data.model

import com.google.firebase.Timestamp

data class Premises(
    val premisesId: String? = null,
    val premisesImageUrl: String? = null,
    val address: String? = null,
    val name: String? = null,
    val users: Map<String, String>? = null,
    val creationDate: Timestamp? = null,
    val contracts: List<String>? = null,
    val invoices: List<String>? = null
)