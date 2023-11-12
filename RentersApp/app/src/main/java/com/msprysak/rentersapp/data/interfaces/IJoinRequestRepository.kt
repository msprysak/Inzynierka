package com.msprysak.rentersapp.data.interfaces

import androidx.lifecycle.LiveData
import com.msprysak.rentersapp.data.model.Request

interface IJoinRequestRepository {

    val requestData: LiveData<List<Request>>
    fun joinRequestListener(): LiveData<List<Request>>
    fun acceptRequest(request: Request,callback: CallBack)

    fun rejectRequest(request: Request,callback: CallBack)

    fun sendJoinRequest(randomCode: String,callback: CallBack)

    fun fetchJoinRequests()
}