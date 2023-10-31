package com.msprysak.rentersapp

import com.msprysak.rentersapp.data.model.User

interface BindUser {
    fun bindUserData(user: User)
}