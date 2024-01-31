package com.matttax.youtubedownloader.youtube.search

import com.github.kiulian.downloader.model.search.SearchResult
import com.matttax.youtubedownloader.core.model.YoutubeStreamable
import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata
import java.util.*
import kotlin.collections.HashMap

class SearchCache constructor(
    private val historyCacheManager: HistoryCacheManager
) {
    private val queries = Collections.synchronizedMap(HashMap<String, SearchResult>())
    private val videos = Collections.synchronizedMap(HashMap<String, YoutubeStreamable>())

    fun putResult(query: String, searchResult: SearchResult) {
        queries[query] = searchResult
    }

    fun getQueryResult(query: String): SearchResult? {
        return queries[query]
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
}
