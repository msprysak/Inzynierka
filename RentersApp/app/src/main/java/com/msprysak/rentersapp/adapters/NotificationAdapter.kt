package com.msprysak.rentersapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.OnItemClickListener
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.RepositorySingleton
import com.msprysak.rentersapp.data.model.Reports
import com.msprysak.rentersapp.data.model.Request
import com.msprysak.rentersapp.databinding.NotificationsJoinRequestItemBinding

class NotificationAdapter(
    private val data: List<Any>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val repository = RepositorySingleton.getInstance()
    private val JOIN_REQUEST_VIEW = 1
    private val REPORT_VIEW = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)

        // Tworzenie widoku ViewHolder w zależności od typu danych
        return when (viewType) {
            JOIN_REQUEST_VIEW -> {
                val binding = NotificationsJoinRequestItemBinding.inflate(inflater, parent, false)
                JoinRequestModelViewHolder(binding, itemClickListener)
            }

            REPORT_VIEW -> {
                val view = inflater.inflate(R.layout.notifications_join_request_item, parent, false)
                ReportsModelViewHolder(view)
            }
            else -> throw IllegalArgumentException("Nieobsługiwany typ widoku")
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        if (holder.itemViewType == JOIN_REQUEST_VIEW) {
            val joinRequestModelViewHolder = holder as JoinRequestModelViewHolder
            val joinRequestModel = item as Request
            joinRequestModelViewHolder.bind(joinRequestModel)

            joinRequestModelViewHolder.itemView.visibility = if (isUserLandlord()) View.VISIBLE else View.GONE

            println("isUserLandlord: ${isUserLandlord()}")

            println("Data bound for JOIN_REQUEST_VIEW")

        } else if (holder.itemViewType == REPORT_VIEW) {
            val reportsModelViewHolder = holder as ReportsModelViewHolder
            val reportsModel = item as Reports
            reportsModelViewHolder.bind(reportsModel)
            println("Data bound for REPORT_VIEW")
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
    class JoinRequestModelViewHolder(
        private val binding: NotificationsJoinRequestItemBinding,
        private val itemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(joinRequestModel: Request) {
            val displayedStatus = if (joinRequestModel.status == "pending") "Status: Oczekujący" else "Status: Zaakceptowany"
            binding.status.text = displayedStatus
            binding.username.text = joinRequestModel.username
            binding.popupMenu.setOnClickListener{
                itemClickListener.onLandlordClick(joinRequestModel)
            }
        }
    }

    class ReportsModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(reportModel: Reports ) {

        }
    }
    private fun isUserLandlord(): Boolean {
        return repository.sharedUserData.value!!.houseRoles!!.values.contains("landlord")
    }
}