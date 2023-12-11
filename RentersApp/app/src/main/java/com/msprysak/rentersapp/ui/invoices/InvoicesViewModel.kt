package com.msprysak.rentersapp.ui.invoices

import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.interfaces.CallBack
import com.msprysak.rentersapp.data.model.Invoice
import com.msprysak.rentersapp.data.model.PdfFile
import com.msprysak.rentersapp.data.repositories.FilesRepository

class InvoicesViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val repository = FilesRepository()

    private var _invoice: MutableLiveData<Invoice> = MutableLiveData()
    val invoice: LiveData<Invoice> get() = _invoice

    private val _invoicesList: MutableLiveData<List<PdfFile>> = MutableLiveData()
    val invoicesList: LiveData<List<PdfFile>> get() = _invoicesList
    init {
        _invoice.value = Invoice()
    }
    fun createCustomPdf(){
        val pdfDocument = PdfDocument()

    }
    fun uploadPdfFile(fileName: String, pdfUri: Uri, callBack: CallBack) {
        repository.uploadPdfFile(fileName, pdfUri,"invoices", callBack)
    }

    fun getInvoices(){
        repository.getFiles("invoices"){
            _invoicesList.value = it
        }
    }

    fun setIssuePlace(issuePlace: String) {
        _invoice.value?.issuePlace = issuePlace
    }

}