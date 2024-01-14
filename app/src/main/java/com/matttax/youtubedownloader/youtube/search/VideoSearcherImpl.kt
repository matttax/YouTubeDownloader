package com.matttax.youtubedownloader.youtube.search

import com.github.kiulian.downloader.YoutubeDownloader
import com.github.kiulian.downloader.downloader.request.RequestSearchContinuation
import com.github.kiulian.downloader.downloader.request.RequestSearchResult
import com.github.kiulian.downloader.model.search.SearchResult
import com.github.kiulian.downloader.model.search.field.TypeField
import com.matttax.youtubedownloader.core.VideoSearcher
import com.matttax.youtubedownloader.core.config.Duration
import com.matttax.youtubedownloader.core.config.SearchConfig
import com.matttax.youtubedownloader.core.config.Uploaded
import com.matttax.youtubedownloader.core.config.ConfigMapper.toYoutubeDuration
import com.matttax.youtubedownloader.core.config.ConfigMapper.toYoutubeSorting
import com.matttax.youtubedownloader.core.config.ConfigMapper.toYoutubeUploaded
import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata
import com.matttax.youtubedownloader.youtube.mappers.VideoDataMapper.toYoutubeVideoMetadata

class VideoSearcherImpl(
    private val downloader: YoutubeDownloader
) : VideoSearcher {

    private var _result: SearchResult? = null

    override fun search(text: String, config: SearchConfig): List<YoutubeVideoMetadata> {
        val request = buildRequest(text, config)
        _result = downloader
            .search(request)
            .data()
        return extractMetadata()
    }

    override fun loadMore(): List<YoutubeVideoMetadata> {
        val result = _result ?: throw SearchException.NoRequestException("No active search request")
        if (!result.hasContinuation()) {
            throw SearchException.NoContinuationException("Video feed continuation not found")
        }
        val nextRequest = RequestSearchContinuation(result)
        _result = downloader.searchContinuation(nextRequest).data()
        return extractMetadata()
    }

    private fun buildRequest(text: String, config: SearchConfig): RequestSearchResult {
        val request = RequestSearchResult(text)
            .type(TypeField.VIDEO)
            .forceExactQuery(!config.allowCorrection)
        config.duration
            .takeIf { it != Duration.ANY }
            ?.let { request.during(it.toYoutubeDuration()) }
        config.uploaded
            .takeIf { it != Uploaded.ANY }
            ?.let { request.uploadedThis(it.toYoutubeUploaded()) }
        request.sortBy(config.sortBy.toYoutubeSorting())
        return request
    }

    private fun extractMetadata(): List<YoutubeVideoMetadata> {
        return _result?.videos()?.map {
            it.toYoutubeVideoMetadata()
        } ?: throw SearchException.SearchFailedException()
    }

}
