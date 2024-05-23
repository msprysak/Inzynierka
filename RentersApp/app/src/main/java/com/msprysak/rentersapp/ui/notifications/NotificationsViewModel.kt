package com.msprysak.rentersapp.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.interfaces.CallBack
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.model.Request
import com.msprysak.rentersapp.data.repositories.JoinRequestRepository

class NotificationsViewModel : ViewModel() {
    private val joinRequestRepository = JoinRequestRepository(UserRepositoryInstance.getInstance().user)
    fun getJoinRequests():LiveData<List<Request>> {
        return joinRequestRepository.joinRequestListener()
    }
    fun acceptJoinRequest(request: Request, callBack: CallBack){
        joinRequestRepository.acceptRequest(request, callBack)
    }
    fun rejectJoinRequest(request: Request, callBack: CallBack){
        joinRequestRepository.rejectRequest(request, callBack)
    }
}