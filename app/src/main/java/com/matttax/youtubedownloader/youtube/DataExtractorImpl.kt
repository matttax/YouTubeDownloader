package com.matttax.youtubedownloader.youtube

import com.github.kiulian.downloader.YoutubeDownloader
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo
import com.matttax.youtubedownloader.core.DataExtractor
import com.matttax.youtubedownloader.core.model.YoutubeStreamable
import com.matttax.youtubedownloader.youtube.mappers.VideoDataMapper.toYoutubeStreamable

class DataExtractorImpl(
    private val downloader: YoutubeDownloader
) : DataExtractor {
    override fun extractStreamable(videoId: String): YoutubeStreamable {
        val request = RequestVideoInfo(videoId)
        val response = downloader.getVideoInfo(request)
        val video = response.data()
        return video.toYoutubeStreamable()
    }
}
