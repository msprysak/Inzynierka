package com.msprysak.rentersapp.data.model

import com.google.firebase.firestore.FieldValue

data class Premises(
    val imageUrl: String? = null,
    val localAddress: String? = null,
    val localName: String? = null,
    val users: Map<String,String>? = null, // Key: userId, Value: role
    val creationDate: FieldValue? = null,
    val contracts: List<String>? = null,
    val invoices: List<String>? = null,
    val temporaryCode: Int? = null
)