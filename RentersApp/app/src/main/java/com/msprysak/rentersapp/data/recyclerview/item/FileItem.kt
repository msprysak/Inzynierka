package com.msprysak.rentersapp.data.recyclerview.item

import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.data.interfaces.OnItemClickListener
import com.msprysak.rentersapp.data.model.PdfFile
import com.msprysak.rentersapp.databinding.ItemFileBinding

class FileItem(private val binding: ItemFileBinding,
               private val onItemClickListener: OnItemClickListener?,
               private val loggedInUserRole: String?) : RecyclerView.ViewHolder(binding.root) {

    fun bindFileRecyclerView(file: PdfFile) {
        val fileName = binding.fileName
        val popupMenuButton = binding.popupMenu

        fileName.text = file.fileName


        if (loggedInUserRole == "landlord") {
             popupMenuButton.setOnClickListener{
                onItemClickListener!!.onLandlordClick(file,popupMenuButton)
            }
        } else{
            popupMenuButton.setOnClickListener{
                onItemClickListener!!.onTenantClick(file,popupMenuButton)
            }
        }
    }
}