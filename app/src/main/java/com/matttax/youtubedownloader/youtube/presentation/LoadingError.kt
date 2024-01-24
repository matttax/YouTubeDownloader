package com.matttax.youtubedownloader.youtube.presentation

sealed class LoadingError {
    data class NoStreamableLinkFound(val reason: String? = null): LoadingError()
    object NoFormatOptionsAvailable: LoadingError()
}
