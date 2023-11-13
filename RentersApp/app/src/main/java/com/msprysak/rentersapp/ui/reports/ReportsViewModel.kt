package com.msprysak.rentersapp.ui.reports

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.interfaces.CallBack
import com.msprysak.rentersapp.data.model.Reports
import com.msprysak.rentersapp.data.repositories.PremisesRepository
import com.msprysak.rentersapp.data.repositories.ReportsRepository

class ReportsViewModel: ViewModel() {

    private val userInstance = UserRepositoryInstance.getInstance()
    private val premisesInstance = PremisesRepository.getInstance(userInstance.getUserData())

    private val repository = ReportsRepository()



    fun createNewReport(report: Reports, uriList : List<Uri>, callBack: CallBack) {
        repository.createNewReport(
            report,
            premisesInstance.getCurrentPremisesId(),
            userInstance.user.value!!.userId!!,
            uriList,
            callBack
        )
    }

    fun setupObserver(){
        return repository.setupReportsListener(premisesInstance.getCurrentPremisesId())
    }

    fun getReports():LiveData<List<Reports>>{
        return repository.getReports()
    }
}