package com.matttax.youtubedownloader.youtube.search

import com.github.kiulian.downloader.model.search.SearchResult
import com.matttax.youtubedownloader.core.config.SearchConfig
import com.matttax.youtubedownloader.core.model.YoutubeStreamable
import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata
import java.util.*
import kotlin.collections.HashMap

class SearchCache(
    private val historyCacheManager: HistoryCacheManager
) {
    private val queries = Collections.synchronizedMap(HashMap<SearchRequest, SearchResult>())
    private val videos = Collections.synchronizedMap(HashMap<String, YoutubeStreamable>())

    fun putResult(
        query: String,
        config: SearchConfig,
        searchResult: SearchResult
    ) {
        queries[buildKey(query, config)] = searchResult
    }

    fun clear(query: String) {
        queries.keys.filter { it.query == query }.forEach {
            queries.remove(it)
        }
    }

    fun clearAllQueries() = queries.clear()

    fun getQueryResult(query: String, config: SearchConfig): SearchResult? {
        return queries[buildKey(query, config)]
    }

    fun putStreamableData(videoId: String, streamable: YoutubeStreamable) {
        videos[videoId] = streamable
    }

    fun getStreamableData(videoId: String) = videos[videoId]

    fun cacheResults(query: String, results: List<YoutubeVideoMetadata>) {
        historyCacheManager.cacheQuery(query, results)
    }

    fun getCachedResult() = historyCacheManager.extractLastQuery()

    fun getCachedQueryText() = historyCacheManager.lastQuery

    private fun buildKey(query: String, config: SearchConfig): SearchRequest {
        return SearchRequest(query, config)
    }
}
