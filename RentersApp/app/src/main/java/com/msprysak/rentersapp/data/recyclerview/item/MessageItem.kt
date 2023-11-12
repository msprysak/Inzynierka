package com.msprysak.rentersapp.data.recyclerview.item

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.model.Message
import com.msprysak.rentersapp.databinding.ItemChatMessageBinding

class MessageItem(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindLeftUser(message: Message) {
        binding.leftChatUsernameText.text = message.senderName
        binding.leftChatMessageText.text = message.message
        if (message.senderPicture.isNotEmpty()){
            Glide.with(binding.root)
                .load(message.senderPicture)
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle)
                .into(binding.leftChatProfileImage)
        }
        binding.rightChatMessageLayout.visibility = RecyclerView.GONE
    }
    fun bindRightUser(message: Message){
        binding.rightChatUsernameText.text = message.senderName
        binding.rightChatMessageText.text = message.message
        if (message.senderPicture.isNotEmpty()){
            Glide.with(binding.root)
                .load(message.senderPicture)
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle)
                .into(binding.rightChatProfileImage)
        }
        binding.leftChatMessageLayout.visibility = RecyclerView.GONE

    }

}