package com.matttax.youtubedownloader.youtube.download

import com.matttax.youtubedownloader.core.model.Format
import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata

data class DownloadingItem(
    val format: Format,
    val metadata: YoutubeVideoMetadata
)
