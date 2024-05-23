package com.msprysak.rentersapp.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object formatDate {

    fun formatDate(date: Date): String {
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date).toString()
    }
}
