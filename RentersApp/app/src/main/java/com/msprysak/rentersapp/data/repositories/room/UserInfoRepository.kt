package com.msprysak.rentersapp.data.repositories.room

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class UserInfoRepository(private val userInfoDao: UserInfoDao) {


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun getUserInfoFlow(id: String): Flow<UserInfo> = userInfoDao.getUserInfo(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateUserInfo(userInfo: UserInfo) {
        userInfoDao.updateUserInfo(userInfo)
    }
}