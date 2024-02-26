package com.matttax.youtubedownloader.youtube.search

import com.matttax.youtubedownloader.core.config.SearchConfig

data class SearchRequest(
    val query: String,
    val config: SearchConfig
)
