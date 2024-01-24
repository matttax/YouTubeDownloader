package com.matttax.youtubedownloader.youtube.search

import com.matttax.youtubedownloader.core.model.YoutubeStreamable
import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata
import java.util.*
import javax.inject.Singleton
import kotlin.collections.HashMap

@Singleton
class SearchCache {

    private val queries = Collections.synchronizedMap(HashMap<String, List<YoutubeVideoMetadata>>())
    private val videos = Collections.synchronizedMap(HashMap<String, YoutubeStreamable>())

    fun putQuery(query: String, list: List<YoutubeVideoMetadata>) {
        queries[query] = list
    }

    fun getQueryResults(query: String): List<YoutubeVideoMetadata>? = queries[query]

    fun putStreamableData(videoId: String, streamable: YoutubeStreamable) {
        videos[videoId] = streamable
    }

    fun getStreamableData(videoId: String) = videos[videoId]
}
