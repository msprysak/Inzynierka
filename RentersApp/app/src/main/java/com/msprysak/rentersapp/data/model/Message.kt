package com.msprysak.rentersapp.data.model

import java.sql.Timestamp


data class Message(val message: String? = null,
                   val senderId: String? = null,
                   val sentAt: java.util.Date = java.util.Date(System.currentTimeMillis())){
    constructor():this("","", Timestamp(System.currentTimeMillis()))
}

