package com.msprysak.rentersapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.data.model.Media
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.data.recyclerview.item.MediaItem
import com.msprysak.rentersapp.databinding.ItemMediaBinding
import com.msprysak.rentersapp.interfaces.OnItemClick

class MediaAdapter(
    private val mediaList: List<Pair<Media,User>>,
    private val onMediaItemClickListener: OnItemClick
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val binding = ItemMediaBinding.inflate(inflater, parent, false)
        return MediaItem(binding)
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MediaItem
        val (media,user) = mediaList[position]

        holder.bindMediaRecyclerView(media)

        holder.itemView.setOnClickListener {
            onMediaItemClickListener.onItemClick(Pair(media, user))
        }
    }
}
