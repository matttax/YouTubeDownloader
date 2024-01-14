package com.matttax.youtubedownloader.youtube.mappers

import com.github.kiulian.downloader.model.videos.formats.AudioFormat
import com.github.kiulian.downloader.model.videos.formats.VideoFormat
import com.github.kiulian.downloader.model.videos.quality.AudioQuality
import com.github.kiulian.downloader.model.videos.quality.VideoQuality
import com.matttax.youtubedownloader.core.model.Format
import com.matttax.youtubedownloader.core.model.YoutubeAudioQuality
import com.matttax.youtubedownloader.core.model.YoutubeVideoQuality

object FormatMapper {

    fun VideoFormat.toYoutubeVideoFormat() = Format.Video(
        url = url(),
        mimeType = mimeType() ?: "",
        duration = duration() ?: 0,
        fps = fps(),
        quality = videoQuality().toYoutubeVideoQuality()
    )

    fun AudioFormat.toYoutubeAudioFormat() = Format.Audio(
        url = url(),
        mimeType = mimeType() ?: "",
        duration = duration() ?: 0,
        quality = audioQuality().toYoutubeAudioQuality()
    )

    private fun VideoQuality.toYoutubeVideoQuality() = when(this) {
        VideoQuality.unknown -> YoutubeVideoQuality.UNKNOWN
        VideoQuality.tiny -> YoutubeVideoQuality.TINY_144
        VideoQuality.small -> YoutubeVideoQuality.SMALL_240
        VideoQuality.medium -> YoutubeVideoQuality.MEDIUM_360
        VideoQuality.large -> YoutubeVideoQuality.LARGE_480
        VideoQuality.hd720 -> YoutubeVideoQuality.HD_720
        VideoQuality.hd1080 -> YoutubeVideoQuality.HD_1080
        VideoQuality.hd1440 -> YoutubeVideoQuality.HD_1440
        VideoQuality.hd2160 -> YoutubeVideoQuality.HD_2160
        VideoQuality.hd2880p -> YoutubeVideoQuality.HD_2880
        VideoQuality.highres -> YoutubeVideoQuality.HIGH_RESOLUTION_3072
        else -> YoutubeVideoQuality.NO_VIDEO
    }

    private fun AudioQuality.toYoutubeAudioQuality() = when(this) {
        AudioQuality.unknown -> YoutubeAudioQuality.UNKNOWN
        AudioQuality.low -> YoutubeAudioQuality.LOW
        AudioQuality.medium -> YoutubeAudioQuality.MEDIUM
        AudioQuality.high -> YoutubeAudioQuality.HIGH
        else -> YoutubeAudioQuality.NO_AUDIO
    }
}