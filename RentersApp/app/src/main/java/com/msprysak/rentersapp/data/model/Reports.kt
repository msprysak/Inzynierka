package com.msprysak.rentersapp.data.model

import com.google.firebase.Timestamp

data class Reports(val reportId: String? = null,
                   val premisesId: String? = null,
                   val userId: String? = null,
                   val reportImages: List<String> = listOf(),
                   val reportTitle: String,
                   val reportComment: String? = null,
                   val reportDescription: String,
                   val reportDate: Timestamp? = Timestamp.now(),
                   val reportStatus: String? = null){
    constructor():this("","","", listOf(),"","", "", Timestamp.now(), "")
}
