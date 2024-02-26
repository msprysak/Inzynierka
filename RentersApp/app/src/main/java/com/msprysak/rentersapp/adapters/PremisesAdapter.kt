package com.msprysak.rentersapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.data.recyclerview.item.PremisesItem
import com.msprysak.rentersapp.databinding.ItemPremisesBinding
import com.msprysak.rentersapp.interfaces.OnPremisesClickListener

class PremisesAdapter(private val premisesList: List<Premises>,
    private val onPremisesClickListener: OnPremisesClickListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val binding = ItemPremisesBinding.inflate(inflater, parent, false)
        return PremisesItem(binding,onPremisesClickListener)
    }

    override fun getItemCount(): Int {
        return premisesList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = premisesList[position]
        if (holder is PremisesItem) {
            holder.bind(item)
        }
    }
}