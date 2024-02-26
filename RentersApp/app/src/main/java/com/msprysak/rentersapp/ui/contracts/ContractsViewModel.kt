package com.msprysak.rentersapp.ui.contracts

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.interfaces.CallBack
import com.msprysak.rentersapp.data.model.PdfFile
import com.msprysak.rentersapp.data.repositories.FilesRepository

class ContractsViewModel: ViewModel() {

    private val repository = FilesRepository()

    private val _contractsList: MutableLiveData<List<PdfFile>> = MutableLiveData()
    val userRole = UserRepositoryInstance.getInstance().user.value!!.houseRoles!!.values.first()
    
    val contractsList: MutableLiveData<List<PdfFile>> get() = _contractsList

    fun uploadPdfFile(fileName: String, pdfUri: Uri, callBack: CallBack) {
        repository.uploadPdfFile(fileName, pdfUri,"contracts", callBack)
    }

    fun getContracts(){
        repository.getFiles("contracts"){
            _contractsList.value = it
        }
    }

    fun downloadFile(item: PdfFile,context: Context , callBack: CallBack) {
        repository.downloadFile(item,context, callBack, "contracts")
    }
    fun deleteFile(item: PdfFile, callBack: CallBack) {
        repository.deletePdfFile(item.fileId.toString(), callBack, "contracts")
    }
}