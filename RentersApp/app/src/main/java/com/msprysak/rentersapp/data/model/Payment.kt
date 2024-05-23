package com.msprysak.rentersapp.data.model

import java.util.Date

data class Payment (
    var paymentId: String? = null,
    var userId: String? = null,
    var paymentTitle: String? = null,
    var paymentAmount: Double? = null,
    var paymentStatus: String? = null,
    var paymentSince: Date? = null,
    var paymentTo: Date? = null,
    var modificationDate: Date? = null,
    val creationDate : Date = Date()
){
    constructor():this("","","",0.0,"", null, null,null)

    fun clear(){
        paymentId = null
        userId = null
        paymentTitle = null
        paymentAmount = null
        paymentStatus = null
        paymentSince = null
        paymentTo = null
    }

}