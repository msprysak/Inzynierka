package com.msprysak.rentersapp.data.recyclerview.item

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.model.Message
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.databinding.ItemChatMessageBinding

class MessageItem(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindLeftUser(pair : Pair<Message, User>) {
        val (message, user) = pair
        binding.leftChatUsernameText.text = user.username
        binding.leftChatMessageText.text = message.message
        if (!user.profilePictureUrl.isNullOrEmpty()){
            Glide.with(binding.root)
                .load(user.profilePictureUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle)
                .into(binding.leftChatProfileImage)
        }
        binding.rightChatMessageLayout.visibility = RecyclerView.GONE
        binding.leftChatMessageLayout.visibility = RecyclerView.VISIBLE
    }
    fun bindRightUser(pair : Pair<Message, User>){
        val (message, user) = pair
        binding.rightChatUsernameText.text = user.username
        binding.rightChatMessageText.text = message.message
        if (!user.profilePictureUrl.isNullOrEmpty()){
            Glide.with(binding.root)
                .load(user.profilePictureUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle)
                .into(binding.rightChatProfileImage)
        }
        binding.leftChatMessageLayout.visibility = RecyclerView.GONE
        binding.rightChatMessageLayout.visibility = RecyclerView.VISIBLE

    }

}