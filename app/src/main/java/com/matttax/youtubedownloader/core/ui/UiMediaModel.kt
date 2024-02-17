package com.matttax.youtubedownloader.core.ui

import androidx.compose.runtime.Immutable

@Immutable
data class UiMediaModel(
    val id: String,
    val thumbnailUri: String,
    val name: String,
    val author: String,
    val duration: Int
)
