package com.msprysak.rentersapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.data.interfaces.OnItemClickListener
import com.msprysak.rentersapp.data.model.PdfFile
import com.msprysak.rentersapp.data.recyclerview.item.FileItem
import com.msprysak.rentersapp.databinding.ItemFileBinding

class FilesAdapter(
    private val filesList: List<PdfFile>,
    private val itemClickListener: OnItemClickListener? = null,
    private val loggedInUserRole: String? = null

): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val binding = ItemFileBinding.inflate(inflater, parent, false)
        return FileItem(binding, itemClickListener, loggedInUserRole)
    }

    override fun getItemCount(): Int {
        return filesList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as FileItem
        val file = filesList[position]
        holder.bindFileRecyclerView(file)

    }
}