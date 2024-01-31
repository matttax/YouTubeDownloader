package com.matttax.youtubedownloader.youtube.search

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata

class HistoryCacheManager(context: Context) {

    private val sharedPrefs = context.getSharedPreferences(CACHE_PREFERENCES, Context.MODE_PRIVATE)
    private val gson = Gson()

    var lastQuery: String? = null
        private set
        get() = sharedPrefs.getString(CACHE_LAST_QUERY, null)

    fun cacheQuery(query: String, result: List<YoutubeVideoMetadata>) {
        lastQuery = query
        val queryResultJson = gson.toJson(result)
        sharedPrefs.edit()
            .putString(CACHE_LAST_QUERY_RESULTS, queryResultJson)
            .putString(CACHE_LAST_QUERY, query)
            .apply()
    }

    fun extractLastQuery(): List<YoutubeVideoMetadata>? {
        val queryResultString = sharedPrefs.getString(CACHE_LAST_QUERY_RESULTS, null)
        val typeToken = object : TypeToken<List<YoutubeVideoMetadata>>(){}.type
        return gson.fromJson(queryResultString, typeToken)
    }

    companion object {
        const val CACHE_PREFERENCES = "cache"
        const val CACHE_LAST_QUERY = "query"
        const val CACHE_LAST_QUERY_RESULTS = "query_results"
    }
}