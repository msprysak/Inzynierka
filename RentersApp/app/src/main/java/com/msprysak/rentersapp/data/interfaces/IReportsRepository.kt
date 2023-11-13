package com.msprysak.rentersapp.data.interfaces

import android.net.Uri
import androidx.lifecycle.LiveData
import com.msprysak.rentersapp.data.model.Reports

interface IReportsRepository {

    fun createNewReport(report: Reports, premisesId: String, userId: String, selectedImages: List<Uri> , callBack: CallBack)
    fun editReport()
    fun deleteReport()
    fun getReports(): LiveData<List<Reports>>

}