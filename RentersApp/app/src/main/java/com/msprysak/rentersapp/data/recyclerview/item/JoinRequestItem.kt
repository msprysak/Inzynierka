package com.msprysak.rentersapp.data.recyclerview.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.data.model.Request
import com.msprysak.rentersapp.databinding.ItemNotificationsJoinRequestBinding
import com.msprysak.rentersapp.interfaces.OnItemClickListener

class JoinRequestItem(
    private val binding: ItemNotificationsJoinRequestBinding,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(joinRequestModel: Request) {
        val displayedStatus = if (joinRequestModel.status == "pending") "Status: Oczekujący" else "Status: Zaakceptowany"
        binding.status.text = displayedStatus
        binding.username.text = joinRequestModel.username

        binding.popupMenu.setOnClickListener {
            itemClickListener.onLandlordClick(joinRequestModel, anchorView = View(it.context))
        }
    }
}