package com.matttax.youtubedownloader.core.model

sealed class Format(
    open val mimeType: String,
    open val duration: Long,
    open val url: String
) {
    data class Video(
        override val mimeType: String = "unknown",
        override val duration: Long = -1,
        override val url: String,
        val quality: YoutubeVideoQuality = YoutubeVideoQuality.UNKNOWN,
        val fps: Int = -1,
    ) : Format(mimeType, duration, url)

    data class Audio(
        override val mimeType: String = "unknown",
        override val duration: Long = -1,
        override val url: String,
        val quality: YoutubeAudioQuality = YoutubeAudioQuality.UNKNOWN,
    ) : Format(mimeType, duration, url)
}

enum class YoutubeAudioQuality(val text: String) {
    NO_AUDIO("Nothing"),
    UNKNOWN("Unknown"),
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

enum class YoutubeVideoQuality(val pixels: Int) {
    NO_VIDEO(-1),
    UNKNOWN(-1),
    TINY_144(144),
    SMALL_240(240),
    MEDIUM_360(360),
    LARGE_480(480),
    HD_720(720),
    HD_1080(1080),
    HD_1440(1440),
    HD_2160(2160),
    HD_2880(2880),
    HIGH_RESOLUTION_3072(3072)
}

enum class MediaFormat(val text: String) {
    VIDEO("Video"),
    AUDIO("Audio")
}
