package com.msprysak.rentersapp.data.recyclerview.item

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.databinding.ItemReportImageBinding

class ReportImageItem(
    private val binding: ItemReportImageBinding,
    private val showCancelButton: Boolean
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(reportImage: Uri) {
        Glide.with(binding.root)
            .load(reportImage)
            .into(binding.image)

        binding.cancel.visibility = if (showCancelButton) {
            RecyclerView.VISIBLE
        } else {
            RecyclerView.GONE
        }
        binding.cancel.setOnClickListener{
            onDeleteClickListener?.invoke()
        }
        binding.image.setOnClickListener{
            onImageClickListener?.invoke()
        }
    }

    private var onDeleteClickListener: (() -> Unit)? = null

    private var onImageClickListener: (() -> Unit)? = null

    fun setOnImageClickListener(listener: () -> Unit) {
        onImageClickListener = listener
    }

    fun setOnDeleteClickListener(listener: () -> Unit) {
        onDeleteClickListener = listener
    }
}