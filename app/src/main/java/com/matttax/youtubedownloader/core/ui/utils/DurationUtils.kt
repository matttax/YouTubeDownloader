package com.matttax.youtubedownloader.core.ui.utils

fun Int.secondsToDuration(): String {
    val hours = div(3600)
    val minutes = mod(3600).div(60)
    val seconds = mod(60)
    return if (hours > 0)
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    else
        String.format("%02d:%02d", minutes, seconds)
}
