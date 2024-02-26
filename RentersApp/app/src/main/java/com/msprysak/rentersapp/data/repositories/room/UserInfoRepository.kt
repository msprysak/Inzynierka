package com.msprysak.rentersapp.data.repositories.room

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class UserInfoRepository(private val userInfoDao: UserInfoDao) {

    val userInfo: Flow<UserInfo> = userInfoDao.getUserInfo()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateUserInfo(userInfo: UserInfo) {
        userInfoDao.updateUserInfo(userInfo)
    }
}