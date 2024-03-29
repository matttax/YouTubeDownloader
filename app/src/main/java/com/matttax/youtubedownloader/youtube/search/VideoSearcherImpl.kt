package com.matttax.youtubedownloader.youtube.search

import com.github.kiulian.downloader.YoutubeDownloader
import com.github.kiulian.downloader.downloader.request.RequestSearchContinuation
import com.github.kiulian.downloader.downloader.request.RequestSearchResult
import com.github.kiulian.downloader.model.search.SearchResult
import com.github.kiulian.downloader.model.search.field.TypeField
import com.matttax.youtubedownloader.core.VideoSearcher
import com.matttax.youtubedownloader.core.config.ConfigMapper.toYoutubeDuration
import com.matttax.youtubedownloader.core.config.ConfigMapper.toYoutubeSorting
import com.matttax.youtubedownloader.core.config.ConfigMapper.toYoutubeUploaded
import com.matttax.youtubedownloader.core.config.Duration
import com.matttax.youtubedownloader.core.config.SearchConfig
import com.matttax.youtubedownloader.core.config.Uploaded
import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata
import com.matttax.youtubedownloader.core.model.mappers.VideoDataMapper.toYoutubeVideoMetadata
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

class VideoSearcherImpl(
    private val downloader: YoutubeDownloader,
    private val searchCache: SearchCache
) : VideoSearcher {

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private var _result = AtomicReference<SearchResult?>(null)

    override fun loadInitial(): Pair<String, List<YoutubeVideoMetadata>> {
        val lastQuery = searchCache.getCachedQueryText()
        lastQuery?.let {
            executorService.execute {
                try {
                    search(it, SearchConfig())
                } catch (ex: Exception) {}
            }
        }
        return searchCache.getCachedResult()
            ?.let {
                (lastQuery ?: DEFAULT_INITIAL_QUERY) to it
            } ?: (DEFAULT_INITIAL_QUERY to search("", SearchConfig()))
    }

    @Synchronized
    override fun search(text: String, config: SearchConfig): List<YoutubeVideoMetadata> {
        searchCache.getQueryResult(text, config)?.let {
            _result.set(it)
        } ?: run {
            val request = buildRequest(text, config)
            _result.set(
                downloader
                    .search(request)
                    .data()
                    .also { result ->
                        result?.let { cache ->
                            searchCache.putResult(text, config, cache)
                        }
                    }
            )
        }
        return extractMetadata().also { searchCache.cacheResults(text, it) }
    }

    override fun refresh(): List<YoutubeVideoMetadata> {
        searchCache.getCachedQueryText()?.let {
            searchCache.clear(it)
        } ?: run {
            searchCache.clearAllQueries()
        }
        return search(searchCache.getCachedQueryText() ?: "", SearchConfig())
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

    companion object {
        const val DEFAULT_INITIAL_QUERY = ""
    }
}
