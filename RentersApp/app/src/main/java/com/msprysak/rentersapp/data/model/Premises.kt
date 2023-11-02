package com.msprysak.rentersapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

data class Premises(
    val premisesImageUrl: String? = null,
    val address: String? = null,
    val name: String? = null,
    val users: Map<String,String>? = null, // Key: userId, Value: role
    val creationDate: Timestamp? = null,
    val contracts: List<String>? = null,
    val invoices: List<String>? = null
)