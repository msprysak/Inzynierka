package com.msprysak.rentersapp.data.model

import java.util.Date

data class Media(
    var mediaImages: List<String> = listOf(),
    var mediaTitle: String = "",
    var mediaDate: Date? = null,
    val userId: String? = null,
    val premisesId: String? = null,
    val creationDate: Date? = null)
