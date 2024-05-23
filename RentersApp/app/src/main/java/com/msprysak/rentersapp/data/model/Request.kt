package com.msprysak.rentersapp.data.model


data class Request(val premisesId: String,
                  val userId: String,
                  val username: String,
                  val status: String){
    constructor():this("","","","")
}