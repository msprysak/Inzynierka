package com.msprysak.rentersapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.interfaces.OnItemClickListener
import com.msprysak.rentersapp.data.model.Reports
import com.msprysak.rentersapp.data.model.Request
import com.msprysak.rentersapp.data.recyclerview.item.JoinRequestItem
import com.msprysak.rentersapp.data.recyclerview.item.ReportsItem
import com.msprysak.rentersapp.databinding.ItemNotificationsJoinRequestBinding

class NotificationAdapter(
    private val data: List<Any>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val repository = UserRepositoryInstance.getInstance()
    private val JOIN_REQUEST_VIEW = 1
    private val REPORT_VIEW = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)

        return when (viewType) {
            JOIN_REQUEST_VIEW -> createJoinRequestViewHolder(inflater, parent)
            REPORT_VIEW -> createReportsViewHolder(parent)
            else -> throw IllegalArgumentException("Nieobsługiwany typ widoku")
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]

        when (holder.itemViewType) {
            JOIN_REQUEST_VIEW -> {
                val joinRequestModelViewHolder = holder as JoinRequestItem
                val joinRequestModel = item as Request
                joinRequestModelViewHolder.bind(joinRequestModel)
                joinRequestModelViewHolder.itemView.visibility = if (isUserLandlord()) View.VISIBLE else View.GONE

            }
            REPORT_VIEW -> {
                val reportsModelViewHolder = holder as ReportsItem
                val reportsModel = item as Reports
                reportsModelViewHolder.bind(reportsModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = data[position]
        return when (item) {
            is Request -> JOIN_REQUEST_VIEW
            is Reports -> REPORT_VIEW
            else -> throw IllegalArgumentException("Nieobsługiwany typ widoku")
        }
    }

    private fun createJoinRequestViewHolder(inflater: LayoutInflater, parent: ViewGroup): JoinRequestItem {
        val binding = ItemNotificationsJoinRequestBinding.inflate(inflater, parent, false)
        return JoinRequestItem(binding, itemClickListener)
    }

    private fun createReportsViewHolder(parent: ViewGroup): ReportsItem {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notifications_join_request, parent, false)
        return ReportsItem(view)
    }

    private fun isUserLandlord(): Boolean {
        return repository.user.value?.houseRoles?.values?.contains("landlord") == true
    }

}
