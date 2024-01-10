package com.matttax.youtubedownloader.domain.model

data class YoutubeVideoMetadata(
    val id: String,
    val name: String,
    val author: String,
    val description: String,
    val viewCount: Long,
    val isLive: Boolean,
    val thumbnailUri: String
)
