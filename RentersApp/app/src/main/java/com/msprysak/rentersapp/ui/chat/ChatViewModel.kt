package com.msprysak.rentersapp.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.model.Message
import com.msprysak.rentersapp.data.repositories.ChatRepository
import com.msprysak.rentersapp.data.repositories.PremisesRepository
import java.sql.Timestamp

class ChatViewModel : ViewModel() {

    private val repository = ChatRepository()
    private val premisesRepository = PremisesRepository.getInstance(UserRepositoryInstance.getInstance().user)
    fun sendMessage(messageText: String) {
        val message = Message(
            message = messageText,
            senderId = UserRepositoryInstance.getInstance().user.value!!.userId.toString(),
            senderName = UserRepositoryInstance.getInstance().user.value!!.username.toString(),
            senderPicture = UserRepositoryInstance.getInstance().user.value!!.profilePictureUrl.toString(),
            sentAt = Timestamp(System.currentTimeMillis())
        )
        repository.sendMessage(message, premisesRepository.getCurrentPremisesId())
    }
    fun fetchMessages(): LiveData<List<Message>> {
        return repository.fetchMessagesByPremisesId(premisesRepository.getCurrentPremisesId())
    }
}