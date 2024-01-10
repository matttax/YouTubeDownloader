package com.matttax.youtubedownloader.youtube.utils

import com.github.kiulian.downloader.model.search.SearchResultVideoDetails
import com.github.kiulian.downloader.model.videos.VideoDetails
import com.github.kiulian.downloader.model.videos.VideoInfo
import com.matttax.youtubedownloader.domain.model.YoutubeStreamable
import com.matttax.youtubedownloader.domain.model.YoutubeVideoMetadata
import com.matttax.youtubedownloader.youtube.utils.FormatMapper.toYoutubeAudioFormat
import com.matttax.youtubedownloader.youtube.utils.FormatMapper.toYoutubeVideoFormat

object VideoDataMapper {

    fun SearchResultVideoDetails.toYoutubeVideoMetadata() = YoutubeVideoMetadata(
        id = videoId() ?: "",
        name = title() ?: "Untitled",
        description = description() ?: "",
        author = author() ?: "Unknown",
        viewCount = viewCount(),
        isLive = isLive,
        thumbnailUri = thumbnails().last() ?: ""
    )

    fun VideoDetails.toYoutubeVideoMetadata() = YoutubeVideoMetadata(
        id = videoId() ?: "",
        name = title() ?: "Untitled",
        description = description() ?: "",
        author = author() ?: "Unknown",
        viewCount = viewCount(),
        isLive = isLive,
        thumbnailUri = thumbnails().last() ?: ""
    )

    fun VideoInfo.toYoutubeStreamable() = YoutubeStreamable (
        metadata = details().toYoutubeVideoMetadata(),
        videoFormats = videoFormats().map { it.toYoutubeVideoFormat() },
        audioFormats = audioFormats().map { it.toYoutubeAudioFormat() }
    )
}
