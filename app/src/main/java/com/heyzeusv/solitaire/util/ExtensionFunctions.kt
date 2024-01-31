package com.heyzeusv.solitaire.util

fun Long.formatTimeDisplay(): String {
    val minutes = this / 60
    val seconds = this % 60

    return String.format("%02d:%02d", minutes, seconds)
}

fun Long.formatTimeStats(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    val hourString = if (hours == 0L) "" else "$hours hours, "

    return "$hourString$minutes minutes, $seconds seconds"
}