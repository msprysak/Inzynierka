package com.msprysak.rentersapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.data.recyclerview.item.TenantItem
import com.msprysak.rentersapp.databinding.ItemTenatBinding
import com.msprysak.rentersapp.interfaces.OnItemClickListener

class TenantsAdapter(
    private val tenants: List<User>,
    private val itemClickListener: OnItemClickListener? = null,
    private val loggedInUserRole: String? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val binding = ItemTenatBinding.inflate(inflater, parent, false)
        return TenantItem(binding, itemClickListener, loggedInUserRole)
    }

    override fun getItemCount(): Int {
        return tenants.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = tenants[position]
        if (holder is TenantItem) {
            holder.bind(item)

        }
    }

}

