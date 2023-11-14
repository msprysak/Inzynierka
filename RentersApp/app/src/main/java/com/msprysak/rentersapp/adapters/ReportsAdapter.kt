package com.msprysak.rentersapp.adapters

import ReportItem
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.data.model.Reports
import com.msprysak.rentersapp.databinding.ItemReportBinding

class ReportsAdapter(
    private val reports: List<Reports>,
)  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val binding = ItemReportBinding.inflate(inflater, parent, false)
        return ReportItem(binding)
    }

    override fun getItemCount(): Int {
        return reports.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ReportItem
        holder.bindReportsRecyclerView(reports[position])

    }
}