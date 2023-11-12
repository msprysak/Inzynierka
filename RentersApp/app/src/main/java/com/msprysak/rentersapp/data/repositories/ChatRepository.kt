package com.msprysak.rentersapp.data.repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.msprysak.rentersapp.data.interfaces.IChatRoomRepository
import com.msprysak.rentersapp.data.model.Message

class ChatRepository: IChatRoomRepository {

    private val DEBUG = "ChatRoomRepository_DEBUG"

    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cloud = FirebaseFirestore.getInstance()
    override fun sendMessage(message: Message, premisesId: String) {
         cloud.collection("chatrooms").document(premisesId)
            .collection("message").document()
            .set(message)
    }

    override fun fetchMessagesByPremisesId(premisesId: String): MutableLiveData<List<Message>> {
        val messagesLiveData = MutableLiveData<List<Message>>()

        cloud.collection("chatrooms").document(premisesId)
            .collection("message").orderBy("sentAt")
            .addSnapshotListener { documentSnapshotm, error ->
                if (error != null) {
                    // Możesz obsłużyć błąd tutaj, np. logując go
                    return@addSnapshotListener
                }

                val messages = mutableListOf<Message>()
                for (doc in documentSnapshotm!!) {
                    val message = doc.toObject(Message::class.java)
                    messages.add(message)
                }

                messagesLiveData.value = messages
            }

        return messagesLiveData
    }

}