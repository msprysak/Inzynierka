package com.msprysak.rentersapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.model.Message
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.data.recyclerview.item.MessageItem
import com.msprysak.rentersapp.databinding.ItemChatMessageBinding

class ChatAdapter(
    private val message: List<Pair<Message,User>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val currentUserId = UserRepositoryInstance.getInstance().user.value!!.userId.toString()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val binding = ItemChatMessageBinding.inflate(inflater, parent, false)
        return MessageItem(binding)

    }

    override fun getItemCount(): Int {
        return message.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MessageItem
        val (message, user) = message[position]
        if (user.userId == currentUserId){
            holder.bindRightUser(Pair(message,user))
        } else{
            holder.bindLeftUser(Pair(message,user))
        }

    }

}