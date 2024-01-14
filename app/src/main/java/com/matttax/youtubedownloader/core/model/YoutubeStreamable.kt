package com.matttax.youtubedownloader.core.model

data class YoutubeStreamable(
    val metadata: YoutubeVideoMetadata,
    val videoFormats: List<Format.Video>,
    val audioFormats: List<Format.Audio>
)
