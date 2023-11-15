package com.msprysak.rentersapp.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class Reports(val reportId: String? = null,
                   val premisesId: String? = null,
                   val userId: String? = null,
                   val reportImages: List<String> = listOf(),
                   val reportTitle: String,
                   val reportComment: String? = null,
                   val reportDescription: String,
                   val reportDate: Timestamp? = Timestamp.now(),
                   val reportStatus: String? = null):Parcelable{
    constructor():this("","","", listOf(),"","", "", Timestamp.now(), "")
}
