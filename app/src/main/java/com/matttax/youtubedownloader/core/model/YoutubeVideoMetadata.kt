package com.matttax.youtubedownloader.core.model

data class YoutubeVideoMetadata(
    val id: String,
    val name: String,
    val author: String,
    val description: String,
    val viewCount: Long,
    val isLive: Boolean,
    val thumbnailUri: String,
    val isMovie: Boolean? = null,
) {
    override fun equals(other: Any?): Boolean {
        return id == (other as? YoutubeVideoMetadata)?.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
