package com.msprysak.rentersapp.data.interfaces

import com.msprysak.rentersapp.data.model.User

interface BindUser {
    fun bindUserData(user: User)
}