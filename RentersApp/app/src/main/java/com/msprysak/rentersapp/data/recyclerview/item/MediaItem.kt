package com.msprysak.rentersapp.data.recyclerview.item

import android.icu.text.SimpleDateFormat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.model.Media
import com.msprysak.rentersapp.databinding.ItemMediaBinding
import java.util.Date
import java.util.Locale

class MediaItem(private val binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindMediaRecyclerView(media: Media) {
        binding.mediaTitle.text = media.mediaTitle
        binding.mediaDateCreated.text = formatDate(media.creationDate!!)
        binding.photoTakenDate.text = media.mediaDate?.let { formatDate(it) } ?: ""
        if (media.mediaImages.isNotEmpty()){
            Glide.with(binding.root)
                .load(media.mediaImages.first())
                .circleCrop()
                .placeholder(R.drawable.ic_report)
                .into(binding.mediaImage)
        }


    }

    private fun formatDate(date: Date): String {
        val javaUtilDate = Date(date.time)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(javaUtilDate)
    }
}