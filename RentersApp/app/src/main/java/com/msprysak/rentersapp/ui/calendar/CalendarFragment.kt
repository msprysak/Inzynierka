package com.msprysak.rentersapp.ui.calendar

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.core.yearMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.adapters.CalendarAdapter
import com.msprysak.rentersapp.data.model.CalendarEvent
import com.msprysak.rentersapp.databinding.CalendarDayLayoutBinding
import com.msprysak.rentersapp.databinding.CalendarHeaderBinding
import com.msprysak.rentersapp.databinding.FragmentCalendarBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID

class CalendarFragment: BaseFragment() {


    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel by viewModels<CalendarViewModel>()

    private var selectedDate: LocalDate? = null
    private val calendarEvents = mutableMapOf<LocalDate, List<CalendarEvent>>()

    private val today = LocalDate.now()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.selectedDateRV.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = eventsAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        }

       updateAdapterForDate(today)
        binding.calendarRecyclerView.monthScrollListener = { month ->
            val monthDisplayName =
                month.yearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
            val formattedMonth = monthDisplayName.substring(0, 1).toUpperCase(Locale.getDefault()) +
                    monthDisplayName.substring(1)
            val result = "$formattedMonth ${month.yearMonth.year}"
            binding.monthYearText.text = result

        }

        binding.nextMonthImage.setOnClickListener {
            binding.calendarRecyclerView.findFirstVisibleMonth()?.let {
                binding.calendarRecyclerView.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        binding.previousMonthImage.setOnClickListener {
            binding.calendarRecyclerView.findFirstVisibleMonth()?.let {
                binding.calendarRecyclerView.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }

        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(200)
        val endMonth = currentMonth.plusMonths(200)
        configureBinders(daysOfWeek)
        binding.calendarRecyclerView.apply {
            setup(startMonth, endMonth, daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

        binding.addButton.setOnClickListener{inputDialog.show()}
    }


    private fun selectDate(date: LocalDate){
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.calendarRecyclerView.notifyDateChanged(it) }
            binding.calendarRecyclerView.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }
    private val eventsAdapter = CalendarAdapter {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.delete_event)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteEvent(it)
            }
            .setNegativeButton(R.string.cancel){
                dialog, _ -> dialog.dismiss()
            }
            .show()
    }
    private fun deleteEvent(event: CalendarEvent) {
        val date = event.date
        calendarEvents[date] = calendarEvents[date].orEmpty().minus(event)
        updateAdapterForDate(date)
    }


    private fun updateAdapterForDate(date: LocalDate) {
        eventsAdapter.apply {
            events.clear()
            events.addAll(this@CalendarFragment.calendarEvents[date].orEmpty())
            notifyDataSetChanged()
        }
        binding.selectedDateText.text = selectionFormatter(date, true)
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayLayoutBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        selectDate(day.date)
                    }
                }
            }
        }
        binding.calendarRecyclerView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            @SuppressLint("Recycle", "ResourceType")
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.binding.dayText
                val rectangleView = container.binding.rectangleView
                val cardViewLayout = container.binding.cardViewLayout

                textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    textView.makeVisible()
                    when (data.date) {
                        today -> {
                           textView.setTextColorRes(context?.obtainStyledAttributes(intArrayOf(
                                com.google.android.material.R.attr.colorOnPrimaryContainer)) ?.getResourceId(0, 0) ?: 0)
                            val attrs = intArrayOf(com.google.android.material.R.attr.colorTertiaryContainer)
                            val typedArray = context?.obtainStyledAttributes(attrs)

                            val colorTertiaryContainer = typedArray?.getColor(0, 0)
                            typedArray?.recycle()

                            cardViewLayout.setCardBackgroundColor(colorTertiaryContainer!!)
                            rectangleView.isVisible = calendarEvents[today].orEmpty().isNotEmpty()
                            if (selectedDate == today) {
                                textView.setTextColor(getColorFromAttribute(com.google.android.material.R.attr.colorOnSecondary))
                                cardViewLayout.setCardBackgroundColor(setColor(com.google.android.material.R.attr.colorSecondary))
                                rectangleView.isVisible = calendarEvents[selectedDate].orEmpty().isNotEmpty()
                            }
                        }
                        selectedDate -> {
                            textView.setTextColor(getColorFromAttribute(com.google.android.material.R.attr.colorOnSecondary))
                            cardViewLayout.setCardBackgroundColor(setColor(com.google.android.material.R.attr.colorSecondary))
                            rectangleView.isVisible = calendarEvents[selectedDate].orEmpty().isNotEmpty()

                        }
                        else -> {
                            textView.setTextColor(getColorFromAttribute(com.google.android.material.R.attr.colorOnPrimaryContainer))
                            cardViewLayout.setCardBackgroundColor(setColor(com.google.android.material.R.attr.colorPrimaryContainer))
                            rectangleView.isVisible = calendarEvents[data.date].orEmpty().isNotEmpty()
                        }
                    }
                } else {
                    textView.makeInVisible()
                    cardViewLayout.makeInVisible()
                    rectangleView.makeInVisible()
                }
            }
        }


        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }
        binding.calendarRecyclerView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = data.yearMonth
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                val dayOfWeek = daysOfWeek[index]
                                val title = dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
                                tv.text = title
                                tv.setTextColorRes(R.color.md_theme_dark_background)
                            }
                    }
                }
            }
    }

    private fun getColorFromAttribute(@AttrRes colorAttribute: Int): Int {
        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(colorAttribute, typedValue, true)
        return typedValue.data
    }
    private fun setColor(@ColorRes colorAttributes: Int): Int {
        val attrs = intArrayOf(colorAttributes)
        val typedArray = context?.obtainStyledAttributes(attrs)
        val color = typedArray?.getColor(0, 0)
        typedArray?.recycle()
        return color!!
    }

    private fun selectionFormatter(date: LocalDate, withDay: Boolean) : String {
        val monthDisplayName = date.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
        val formattedMonth = monthDisplayName.substring(0, 1).toUpperCase(Locale.getDefault()) +
                monthDisplayName.substring(1)
        return if (withDay)
            "${date.dayOfMonth} $formattedMonth ${date.year}"
        else
            "$formattedMonth ${date.yearMonth.year}"
    }


    private val inputDialog by lazy {
        val editText = AppCompatEditText(requireContext())
        val layout = FrameLayout(requireContext()).apply {
            val padding = dpToPx(20, requireContext())
            setPadding(padding, padding, padding, padding)
            addView(editText, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.new_event))
            .setView(layout)
            .setPositiveButton(R.string.save) { _, _ ->
                saveEvent(editText.text.toString())
                editText.setText("")
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
            .apply {
                setOnShowListener {
                    // Show the keyboard
                    editText.requestFocus()
                    context.inputMethodManager
                        .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
                setOnDismissListener {
                    // Hide the keyboard
                    dismiss()
                }
            }

    }

    private fun saveEvent(text: String) {
        if (text.isBlank()) {
            Toast.makeText(requireContext(), R.string.next, Toast.LENGTH_LONG)
                .show()
        } else {
            selectedDate?.let {
                calendarEvents[it] =
                    calendarEvents[it].orEmpty().plus(CalendarEvent(UUID.randomUUID().toString(), text, it))
                println("Calendar event date: $it and text: $text")
                calendarViewModel.addTask(text, it)
                updateAdapterForDate(it)
            }
        }
    }

}
