package com.msprysak.rentersapp.interfaces

import com.msprysak.rentersapp.data.model.User

interface BindUser {
    fun bindUserData(user: User)
}