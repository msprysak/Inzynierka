package com.msprysak.rentersapp.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.data.recyclerview.item.ReportImageItem
import com.msprysak.rentersapp.databinding.ItemReportImageBinding

class ReportsPhotoAdapter(
    private val photos: MutableList<Uri>,
    private val onDeleteClickListener: (Uri) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val binding = ItemReportImageBinding.inflate(inflater, parent, false)
        return ReportImageItem(binding)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ReportImageItem
        val item = photos[position]
        holder.bind(item)
        holder.setOnDeleteClickListener {
            onDeleteClickListener(item)
        }

    }
    fun updateList(newPhotos: List<Uri>) {
        photos.clear()
        photos.addAll(newPhotos)
        notifyDataSetChanged()
    }

}