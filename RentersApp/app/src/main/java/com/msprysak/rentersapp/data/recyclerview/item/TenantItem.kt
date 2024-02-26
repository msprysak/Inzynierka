package com.msprysak.rentersapp.data.recyclerview.item

import android.annotation.SuppressLint
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.databinding.ItemTenatBinding
import com.msprysak.rentersapp.interfaces.OnItemClickListener

class TenantItem(
    private val binding: ItemTenatBinding,
    private val onItemClickListener: OnItemClickListener?,
    private val loggedInUserRole: String?
): RecyclerView.ViewHolder(binding.root) {
    private val popupMenuButton: View = binding.popupMenu

    @SuppressLint("SetTextI18n")
    fun bind(user: User){


        binding.tenatsUsername.text = user.username
        if (user.phoneNumber.isNullOrEmpty()){
            binding.phoneNumberPlaceholder.visibility = View.GONE
            binding.tenatsPhonenumberIcon.visibility = View.GONE
            val params = binding.emailIcon.layoutParams as ConstraintLayout.LayoutParams
            params.topToBottom = binding.roleTextView.id
            binding.emailIcon.layoutParams = params
        }else{
            binding.phoneNumberPlaceholder.text = user.phoneNumber
        }
        if (user.houseRoles?.containsValue("landlord") != true) {
            binding.roleTextView.text = "Wynajmujący"
        }

        binding.emailTextView.text = user.email
        binding.phoneNumberPlaceholder.text = user.phoneNumber
        if (!user.profilePictureUrl.isNullOrEmpty()) {
            Glide.with(binding.root)
                .load(user.profilePictureUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle)
                .into(binding.tenatsProfilePicture)
        }

        if (loggedInUserRole == "landlord") {
            popupMenuButton.setOnClickListener{
                onItemClickListener!!.onLandlordClick(user,popupMenuButton)
            }
        } else{
            popupMenuButton.setOnClickListener{
                onItemClickListener!!.onTenantClick(user,popupMenuButton)
            }
        }


    }
}