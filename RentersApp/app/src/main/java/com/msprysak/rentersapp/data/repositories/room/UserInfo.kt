package com.msprysak.rentersapp.data.repositories.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_info_table")
data class UserInfo(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "user_name_surname")
    val userNameSurname: String,
    @ColumnInfo(name = "user_nip_pesel")
    val userNipPesel: String,
    @ColumnInfo(name = "user_street")
    val userStreet: String,
    @ColumnInfo(name = "user_postal_code")
    val userPostalCode: String,
    @ColumnInfo(name = "user_city")
    val userCity: String,
    @ColumnInfo(name = "premises_id")
    val premisesId: String

)