package com.msprysak.rentersapp.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.data.model.CalendarEvent
import com.msprysak.rentersapp.databinding.CalendarEventItemBinding
import com.msprysak.rentersapp.ui.calendar.layoutInflater

class CalendarAdapter(val onClick: (CalendarEvent) -> Unit) : RecyclerView.Adapter<CalendarAdapter.CalendarEventViewHolder>() {

    val events = mutableListOf<CalendarEvent>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarEventViewHolder {
        return CalendarEventViewHolder(
            CalendarEventItemBinding.inflate(parent.context.layoutInflater, parent, false),
        )
    }

    override fun onBindViewHolder(viewHolder: CalendarEventViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    inner class CalendarEventViewHolder(private val binding: CalendarEventItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onClick(events[bindingAdapterPosition])
            }
        }

        fun bind(event: CalendarEvent) {
            binding.itemEventText.text = event.text
        }
    }
}
