package com.matttax.youtubedownloader.youtube.presentation.ui.utils

data class Options<T>(
    val argumentMap: Map<String, T>,
    val action: (T) -> Unit
)

class UnknownFieldException(override val message: String?) : NoSuchElementException(message)
