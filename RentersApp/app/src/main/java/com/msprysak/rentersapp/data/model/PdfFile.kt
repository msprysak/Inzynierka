package com.msprysak.rentersapp.data.model

data class PdfFile(
    var fileName: String? = null,
    var fileId: String? = null,
    var fileUrl: String? = null,
    var fileCreationDate: String? = null,
    var content: String? = null,
    var fileOwner: String? = null,
    var fileAssignedUser: User? = null
)
