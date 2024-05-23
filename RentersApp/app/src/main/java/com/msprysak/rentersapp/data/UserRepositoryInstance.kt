package com.msprysak.rentersapp.data

import com.msprysak.rentersapp.data.repositories.UserRepository

object UserRepositoryInstance {
    private var instance: UserRepository? = null

    fun getInstance(): UserRepository {
        if (instance == null) {
            synchronized(UserRepositoryInstance::class.java) {
                if (instance == null) {
                    instance = UserRepository()
                }
            }
        }
        return instance!!
    }
}