package com.msprysak.rentersapp.ui.invoices

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.model.PdfFile
import com.msprysak.rentersapp.data.repositories.FilesRepository
import com.msprysak.rentersapp.data.repositories.room.UserInfo
import com.msprysak.rentersapp.data.repositories.room.UserInfoRepository
import com.msprysak.rentersapp.interfaces.CallBack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InvoicesViewModel(private val roomRepository: UserInfoRepository) : ViewModel() {

    private val repository = FilesRepository()

    private val _invoicesList: MutableLiveData<List<PdfFile>> = MutableLiveData()
    val invoicesList: LiveData<List<PdfFile>> get() = _invoicesList
    val userRole = UserRepositoryInstance.getInstance().user.value!!.houseRoles!!.values.first()

    val userInfo = roomRepository.userInfo.asLiveData()
    suspend fun updateUserInfo(userInfo: UserInfo){
        withContext(Dispatchers.IO) {
            roomRepository.updateUserInfo(userInfo)
        }
    }

    fun uploadPdfFile(fileName: String, pdfUri: Uri, callBack: CallBack) {
        repository.uploadPdfFile(fileName, pdfUri, "invoices", callBack)
    }

    fun downloadFile(item: PdfFile, context: Context, callBack: CallBack) {
        repository.downloadFile(item, context, callBack, "invoices")
    }

    fun getInvoices() {
        repository.getFiles("invoices") {
            _invoicesList.value = it
        }
    }

    fun deleteFile(item: PdfFile, callBack: CallBack) {
        repository.deletePdfFile(item.fileId.toString(), callBack, "invoices")
    }


}

class InvoicesViewModelFactory(private val repository: UserInfoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InvoicesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InvoicesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}