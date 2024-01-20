package com.matttax.youtubedownloader.youtube.presentation.states

data class UriSelectionState(
    val formatOptions: List<String>,
    val qualityOptions: List<String>,
    val mimeOptions: List<String>,
    val selectedFormat: String,
    val selectedQuality: String,
    val selectedMime: String
)
