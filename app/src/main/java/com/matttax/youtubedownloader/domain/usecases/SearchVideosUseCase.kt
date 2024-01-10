package com.matttax.youtubedownloader.domain.usecases

import com.matttax.youtubedownloader.domain.VideoSearcher
import com.matttax.youtubedownloader.domain.config.SearchConfig
import com.matttax.youtubedownloader.domain.exceptions.SearchException
import com.matttax.youtubedownloader.domain.model.YoutubeVideoMetadata

class SearchVideosUseCase(
    private val videoSearcher: VideoSearcher
) {
    fun executeSearch(text: String, config: SearchConfig) = videoSearcher.search(text, config)

    fun searchFurther(): List<YoutubeVideoMetadata> {
        return try {
            videoSearcher.loadMore()
        } catch (ex: SearchException.NoContinuationException) {
            emptyList()
        }
    }
}
