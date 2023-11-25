package com.msprysak.rentersapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.databinding.ItemPaymentSelectUserBinding

class PaymentsSelectUsersAdapter(
    context: Context,
    private val users: List<User>
) : ArrayAdapter<User>(context, 0, users) {

    private val selectedUsers = HashSet<User>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemPaymentSelectUserBinding

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            binding = ItemPaymentSelectUserBinding.inflate(inflater, parent, false)
            binding.root.tag = binding
        } else {
            binding = convertView.tag as ItemPaymentSelectUserBinding
        }

        val user = getItem(position)

        user?.let {
            Glide.with(context)
                .load(user.profilePictureUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle)
                .into(binding.userImage)

            binding.username.text = user.username
            binding.checkbox.isChecked = selectedUsers.contains(user)

            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedUsers.add(user)
                } else {
                    selectedUsers.remove(user)
                }
            }
        }

        return binding.root
    }

    fun getSelectedUsers(): MutableSet<User> {
        return selectedUsers
    }
}