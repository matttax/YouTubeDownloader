package com.matttax.youtubedownloader.youtube.search

import com.github.kiulian.downloader.model.search.SearchResult
import com.matttax.youtubedownloader.core.model.YoutubeStreamable
import java.util.*
import kotlin.collections.HashMap

class SearchCache(
    private val cacheManager: CacheManager
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
}
