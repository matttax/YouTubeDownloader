package com.matttax.youtubedownloader.youtube.presentation.ui.model

import androidx.compose.runtime.Immutable
import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata

@Immutable
data class UiMediaModel(
    val id: String,
    val thumbnailUri: String,
    val name: String,
    val author: String
)

fun YoutubeVideoMetadata.toUiModel(): UiMediaModel {
    return UiMediaModel(
        id, thumbnailUri, name, author
    )
}