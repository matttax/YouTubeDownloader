package com.matttax.youtubedownloader.core

import com.matttax.youtubedownloader.core.config.SearchConfig
import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata

interface VideoSearcher {
    fun loadInitial(): Pair<String, List<YoutubeVideoMetadata>>
    fun search(text: String, config: SearchConfig): List<YoutubeVideoMetadata>
    fun refresh(): List<YoutubeVideoMetadata>
    fun loadMore(): List<YoutubeVideoMetadata>
}
