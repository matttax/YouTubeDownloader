package com.matttax.youtubedownloader.core

fun <T> MutableList<T>.shift(from: Int, to: Int) {
    if (from < to) {
        for (i in from until to) {
            add(i, removeAt(i + 1))
        }
    } else {
        for (i in from downTo to + 1) {
            add(i, removeAt(i - 1))
        }
    }
}
