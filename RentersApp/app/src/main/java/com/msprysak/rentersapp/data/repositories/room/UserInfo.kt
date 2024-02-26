package com.msprysak.rentersapp.data.repositories.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info_table")
class UserInfo (
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    @ColumnInfo(name = "user_name_surname") val userNameSurname: String,
    @ColumnInfo(name = "user_nip") val userNip: String,
    @ColumnInfo(name = "user_street") val userStreet: String,
    @ColumnInfo(name = "user_postal_code") val userPostalCode: String,
    @ColumnInfo(name = "user_city") val userCity: String

)