package com.msprysak.rentersapp.ui.invoices

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.model.PdfFile
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.data.repositories.FilesRepository
import com.msprysak.rentersapp.data.repositories.PremisesRepository
import com.msprysak.rentersapp.data.repositories.room.UserInfo
import com.msprysak.rentersapp.data.repositories.room.UserInfoRepository
import com.msprysak.rentersapp.interfaces.CallBack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InvoicesViewModel(private val roomRepository: UserInfoRepository) : ViewModel() {

    private val repository = FilesRepository()

    private val _invoicesList: MutableLiveData<List<PdfFile>> = MutableLiveData()
    val invoicesList: LiveData<List<PdfFile>> get() = _invoicesList

    private val userId = UserRepositoryInstance.getInstance().user.value!!.userId
    val premises = PremisesRepository.getInstance(UserRepositoryInstance.getInstance().user)
    val userRole = UserRepositoryInstance.getInstance().user.value!!.houseRoles!!.values.first()

    private val _usersListData: MutableLiveData<List<User>> = MutableLiveData()
    val usersListData: LiveData<List<User>> get() = _usersListData



    private val _userInfoFlow = MutableStateFlow<UserInfo?>(null)
    val userInfoFlow: StateFlow<UserInfo?> get() = _userInfoFlow

    fun fetchUsers(){

        premises.fetchUsers{
            _usersListData.postValue(it)
        }
    }
    init {
        getUserInfo(userId!!)
    }
    fun getUserInfo(id: String) {
        viewModelScope.launch {
            roomRepository.getUserInfoFlow(id).collect {
                _userInfoFlow.value = it
            }
        }
    }



    suspend fun updateUserInfo(sellerNameSurname: String, sellerNip: String, sellerStreet: String, sellerPostalCode: String, sellerCity: String) {
        val userInfo = com.msprysak.rentersapp.data.repositories.room.UserInfo(
            id = userId!!,
            userNameSurname = sellerNameSurname,
            userNipPesel = sellerNip,
            userStreet = sellerStreet,
            userPostalCode = sellerPostalCode,
            userCity = sellerCity,
            premisesId = premises.premises.value!!.premisesId.toString()
        )

        println("User info: ${userInfo.userCity}")
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
        repository.deletePdfFile(item.fileId.toString(), callBack)
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