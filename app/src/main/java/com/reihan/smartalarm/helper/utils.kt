package com.reihan.smartalarm.helper

import java.text.SimpleDateFormat
import java.time.Month
import java.util.*

fun timeFormatter(hourOfDay: Int, minute: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(0, 0, 0, hourOfDay, minute)

    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
}

fun dateFormatter(year: Int, month: Int, dayOfMonth: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, dayOfMonth)

    return SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(calendar.time)

}