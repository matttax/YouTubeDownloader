package com.matttax.youtubedownloader.youtube.presentation.states

data class UriSelectionState(
    val formatOptions: List<String> = emptyList(),
    val qualityOptions: List<String> = emptyList(),
    val mimeOptions: List<String> = emptyList(),
    val selectedFormat: String = "",
    val selectedQuality: String = "",
    val selectedMime: String = ""
)
