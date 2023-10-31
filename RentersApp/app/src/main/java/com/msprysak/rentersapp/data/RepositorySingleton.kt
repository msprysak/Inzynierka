package com.msprysak.rentersapp.data

object RepositorySingleton {
    private var instance: FirebaseRepository? = null

    fun getInstance(): FirebaseRepository {
        if (instance == null) {
            synchronized(RepositorySingleton::class.java) {
                if (instance == null) {
                    instance = FirebaseRepository()
                }
            }
        }
        return instance!!
    }
}