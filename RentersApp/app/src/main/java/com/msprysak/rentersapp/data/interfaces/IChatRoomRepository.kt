package com.msprysak.rentersapp.data.interfaces

import androidx.lifecycle.LiveData
import com.msprysak.rentersapp.data.model.Message

interface IChatRoomRepository {
    fun sendMessage(message: Message, premisesId: String)
    fun fetchMessagesByPremisesId(premisesId: String): LiveData<List<Message>>
}