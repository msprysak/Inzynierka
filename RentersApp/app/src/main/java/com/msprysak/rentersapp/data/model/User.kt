package com.msprysak.rentersapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class that captures user information for logged in users retrieved from FirebaseRepository
 */

@Parcelize
data class User(
    val userId: String? = null,
    val username: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val profilePictureUrl: String? = null,
    val houseRoles: Map<String,String>? = null // Key: premisesId, Value: role

):Parcelable