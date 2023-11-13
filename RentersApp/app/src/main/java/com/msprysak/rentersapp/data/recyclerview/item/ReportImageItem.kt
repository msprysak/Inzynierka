package com.msprysak.rentersapp.data.recyclerview.item

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.databinding.ItemReportImageBinding

class ReportImageItem(
    private val binding: ItemReportImageBinding
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(reportImage: Uri) {
        binding.image.setImageURI(reportImage)

        binding.cancel.setOnClickListener{
            onDeleteClickListener?.invoke()
        }
    }

    private var onDeleteClickListener: (() -> Unit)? = null

    fun setOnDeleteClickListener(listener: () -> Unit) {
        onDeleteClickListener = listener
    }
}