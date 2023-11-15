package com.msprysak.rentersapp.data.interfaces

import android.net.Uri
import androidx.lifecycle.LiveData
import com.msprysak.rentersapp.data.model.Reports
import com.msprysak.rentersapp.data.model.User

interface IReportsRepository {

    fun createNewReport(report: Reports, premisesId: String, userId: String, selectedImages: List<Uri> , callBack: CallBack)
    fun editReport()
    fun deleteReport()

    fun setupReportsListener(premisesId: String): LiveData<List<Pair<Reports, User>>>
//    fun getReports(): LiveData<List<Pair<Reports,User>>>

}