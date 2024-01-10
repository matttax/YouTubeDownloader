package com.matttax.youtubedownloader.domain.model

sealed class Format(
    open val mimeType: String,
    open val duration: Long,
    open val url: String
) {
    data class Video(
        override val mimeType: String,
        override val duration: Long,
        override val url: String,
        val quality: YoutubeVideoQuality,
        val fps: Int,
    ) : Format(mimeType, duration, url)

    data class Audio(
        override val mimeType: String,
        override val duration: Long,
        override val url: String,
        val quality: YoutubeAudioQuality,
    ) : Format(mimeType, duration, url)

}

enum class YoutubeAudioQuality {
    NO_AUDIO, UNKNOWN, LOW, MEDIUM, HIGH
}

enum class YoutubeVideoQuality {
    NO_VIDEO,
    UNKNOWN,
    TINY_144,
    SMALL_240,
    MEDIUM_360,
    LARGE_480,
    HD_720,
    HD_1080,
    HD_1440,
    HD_2160,
    HD_2880,
    HIGH_RESOLUTION_3072
}
