package com.msprysak.rentersapp.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.msprysak.rentersapp.data.model.Message
import com.msprysak.rentersapp.data.model.User

class ChatRepository{

    private val DEBUG = "ChatRoomRepository_DEBUG"

    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cloud = FirebaseFirestore.getInstance()
    fun sendMessage(message: Message, premisesId: String) {
         cloud.collection("chatrooms").document(premisesId)
            .collection("message").document()
            .set(message)
    }

     fun fetchMessagesByPremisesId(premisesId: String): LiveData<List<Pair<Message, User>>> {
        val messagesLiveData = MutableLiveData<List<Pair<Message, User>>>()

        cloud.collection("chatrooms").document(premisesId)
            .collection("message").orderBy("sentAt", Query.Direction.ASCENDING)
            .addSnapshotListener { documentSnapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val messages = mutableListOf<Pair<Message, User>>()
                val tasks = mutableListOf<Task<DocumentSnapshot>>()

                for (doc in documentSnapshot!!) {
                    val message = doc.toObject(Message::class.java)
                    val userTask = cloud.collection("users").document(message.senderId!!)
                        .get()

                    tasks.add(userTask)
                }

                Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
                    .addOnSuccessListener { userSnapshots ->
                        for ((index, doc) in documentSnapshot.withIndex()) {
                            val message = doc.toObject(Message::class.java)
                            val user = userSnapshots[index].toObject(User::class.java)
                            messages.add(Pair(message!!, user!!))
                        }
                        messagesLiveData.postValue(messages)
                    }
            }

        return messagesLiveData
    }

}