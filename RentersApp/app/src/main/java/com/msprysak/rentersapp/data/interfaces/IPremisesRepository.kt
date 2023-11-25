package com.msprysak.rentersapp.data.interfaces

import androidx.lifecycle.LiveData
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.data.model.User

interface IPremisesRepository {

    fun createNewPremises(premises: Premises, user: User, callback: CallBack)
    fun editPremisesData(map: Map<String,String>)
    fun getPremisesData(): LiveData<Premises>

    fun addTemporaryCode(randomCode: String)
    fun getUsersByIds(ids: List<String>) : LiveData<List<User>>

    fun fetchUsers(callback: (List<User>) -> Unit)

    fun uploadPremisesPhoto(bytes: ByteArray)

    fun deleteUserFromPremises(users: User)

}