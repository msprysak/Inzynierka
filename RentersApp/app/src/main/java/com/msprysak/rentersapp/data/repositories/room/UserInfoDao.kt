package com.msprysak.rentersapp.data.repositories.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import javax.annotation.Nullable


@Dao
interface UserInfoDao {

    @Query("SELECT * FROM user_info_table")
    fun getUserInfo(): Flow<UserInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateUserInfo(userInfo: UserInfo)


    @Delete
    fun deleteUserInfo(@Nullable userInfo: UserInfo?): Int


}