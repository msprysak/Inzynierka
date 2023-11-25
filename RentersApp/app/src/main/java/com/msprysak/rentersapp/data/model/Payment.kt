package com.msprysak.rentersapp.data.model

import java.sql.Timestamp

data class Payment (
    var paymentId: String? = null,
    var userId: String? = null,
    var paymentTitle: String? = null,
    var paymentAmount: Double? = null,
    var paymentStatus: String? = null,
    var paymentSince: Timestamp? = null,
    var paymentTo: Timestamp? = null,
    val creationDate : java.util.Date = java.util.Date(System.currentTimeMillis())
){
    constructor():this("","","",0.0,"", null, null)}