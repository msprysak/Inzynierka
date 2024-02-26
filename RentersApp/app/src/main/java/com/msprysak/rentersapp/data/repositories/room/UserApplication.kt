package com.msprysak.rentersapp.data.repositories.room

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class UserApplication: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

        // Inicjalizacja bazy danych
        val database by lazy { UserInfoRoomDatabase.getDatabase(this, applicationScope) }
        val roomRepository by lazy { UserInfoRepository(database.userInfoDao()) }

}