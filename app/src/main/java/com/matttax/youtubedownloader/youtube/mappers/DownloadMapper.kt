package com.matttax.youtubedownloader.youtube.mappers

import com.matttax.youtubedownloader.core.model.YoutubeStreamable
import com.matttax.youtubedownloader.youtube.download.model.DownloadLink
import com.matttax.youtubedownloader.youtube.download.model.MediaStreamingOptions

fun YoutubeStreamable.getStreamingOptions(): MediaStreamingOptions {
    val videoLinks = videoFormats
        .sortedBy { it.quality.pixels }
        .groupBy { it.quality }
        .mapValues {
            it.value.map { video -> DownloadLink(video.mimeType, video.url) }
        }
    val audioLinks = audioFormats
        .sortedBy { it.quality.ordinal }
        .groupBy { it.quality }
        .mapValues {
            it.value.map { video -> DownloadLink(video.mimeType, video.url) }
        }
    return MediaStreamingOptions(videoLinks, audioLinks)
}
