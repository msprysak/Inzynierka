package com.msprysak.rentersapp.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.CallBack
import com.msprysak.rentersapp.data.RepositorySingleton
import com.msprysak.rentersapp.data.model.Request

class NotificationsViewModel : ViewModel() {
    private val repository = RepositorySingleton.getInstance()
    fun getJoinRequests():LiveData<List<Request>> {
        return repository.activateJoinRequestsListener()
    }
    fun acceptJoinRequest(request: Request, callBack: CallBack){
        repository.acceptJoinRequest(request, callBack)
    }
    fun rejectJoinRequest(request: Request, callBack: CallBack){
        repository.rejectJoinRequest(request, callBack)
    }
}