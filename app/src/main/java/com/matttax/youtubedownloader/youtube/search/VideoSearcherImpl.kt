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
import com.matttax.youtubedownloader.core.model.VideoDataMapper.toYoutubeVideoMetadata
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

class VideoSearcherImpl(
    private val downloader: YoutubeDownloader,
    private val searchCache: SearchCache
) : VideoSearcher {

    private var _result = AtomicReference<SearchResult?>(null)

    override fun loadInitial(): List<YoutubeVideoMetadata> {
        searchCache.getCachedQueryText()?.let {
            thread {
                search(it, SearchConfig())
            }
        }
        return searchCache.getCachedResult() ?: search("", SearchConfig())
    }

    @Synchronized
    override fun search(text: String, config: SearchConfig): List<YoutubeVideoMetadata> {
        searchCache.getQueryResult(text)?.let {
            _result.set(it)
        } ?: run {
            val request = buildRequest(text, config)
            _result.set(
                downloader
                    .search(request)
                    .data()
                    .also { it?.let { cache -> searchCache.putResult(text, cache) } }
            )
        }
        return extractMetadata().also { searchCache.cacheResults(text, it) }
    }

    @Synchronized
    override fun loadMore(): List<YoutubeVideoMetadata> {
        val result = _result.get() ?: throw SearchException.NoRequestException("No active search request")
        if (!result.hasContinuation()) {
            throw SearchException.NoContinuationException("Video feed continuation not found")
        }
        val nextRequest = RequestSearchContinuation(result)
        downloader.searchContinuation(nextRequest).data()?.let { _result.set(it) }
            ?: throw SearchException.SearchFailedException()
        return extractMetadata()
    }

    @Synchronized
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

    @Synchronized
    private fun extractMetadata(): List<YoutubeVideoMetadata> {
        return _result.get()?.videos()?.map {
            it.toYoutubeVideoMetadata()
        } ?: throw SearchException.SearchFailedException()
    }

}
