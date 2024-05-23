package com.msprysak.rentersapp.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.data.recyclerview.item.ReportImageItem
import com.msprysak.rentersapp.databinding.ItemReportImageBinding

class ReportsPhotoAdapter(
    private val photos: MutableLiveData<List<Uri>>,
    private val showCancelButton: Boolean,
    private val onDeleteClickListener: (Uri) -> Unit,
    private val onImageClick: (Uri) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val binding = ItemReportImageBinding.inflate(inflater, parent, false)
        return ReportImageItem(binding, showCancelButton)
    }

    override fun getItemCount(): Int {
        return photos.value?.size ?: 0    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ReportImageItem
        val item = photos.value!![position]
        holder.bind(item)
        holder.setOnDeleteClickListener {
            onDeleteClickListener(item)
        }
        if (!showCancelButton){
            holder.setOnImageClickListener {
                onImageClick(item)
            }

        }

    }
    fun updateList(newPhotos: List<Uri>) {
        photos.value = newPhotos
        notifyDataSetChanged()
    }

}