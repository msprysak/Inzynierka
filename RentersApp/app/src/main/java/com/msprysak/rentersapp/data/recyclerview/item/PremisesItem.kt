package com.msprysak.rentersapp.data.recyclerview.item

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.databinding.ItemPremisesBinding
import com.msprysak.rentersapp.interfaces.OnPremisesClickListener

class PremisesItem(private val binding: ItemPremisesBinding,
    private val popupClickListener: OnPremisesClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(premises: Premises) {
        binding.premisesName.text = premises.name
        if (premises.address != null) {
            binding.premisesAddress.text = premises.address
        } else{
            binding.premisesAddress.visibility = RecyclerView.GONE
        }
        Glide.with(binding.root)
            .load(premises.premisesImageUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_home)
            .into(binding.premisesImage)

        binding.popupMenu.setOnClickListener {
            popupClickListener.onPremisesClick(premises, anchorView = it)
        }
    }
}