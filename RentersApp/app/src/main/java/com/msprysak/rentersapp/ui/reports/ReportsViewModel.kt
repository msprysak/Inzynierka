package com.msprysak.rentersapp.ui.reports

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.interfaces.CallBack
import com.msprysak.rentersapp.data.model.Reports
import com.msprysak.rentersapp.data.model.User
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

    fun setupObserver(): LiveData<List<Pair<Reports, User>>> {
        return repository.setupReportsListener(premisesInstance.getCurrentPremisesId())
    }
}