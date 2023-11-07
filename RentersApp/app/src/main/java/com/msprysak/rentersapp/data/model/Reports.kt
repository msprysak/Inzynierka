package com.msprysak.rentersapp.data.model

data class Reports(private val reportId: String,
                   private val premisesId: String,
                   private val userId: String,
                   private val reportDescription: String,
                   private val reportDate: String,
                   private val reportStatus: String)
