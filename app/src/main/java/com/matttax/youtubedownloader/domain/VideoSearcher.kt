package com.matttax.youtubedownloader.domain

import com.matttax.youtubedownloader.domain.config.SearchConfig
import com.matttax.youtubedownloader.domain.model.YoutubeVideoMetadata

interface VideoSearcher {
    fun search(text: String, config: SearchConfig): List<YoutubeVideoMetadata>
    fun loadMore(): List<YoutubeVideoMetadata>
}