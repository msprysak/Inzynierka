package com.msprysak.rentersapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PdfFile(
    var fileName: String? = null,
    var fileId: String? = null,
    var fileUrl: String? = null,
    var fileCreationDate: String? = null,
    var content: String? = null,
    var fileOwner: String? = null,
    var fileAssignedUser: User? = null
) : Parcelable
