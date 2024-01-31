package com.matttax.youtubedownloader.youtube.usecases

import com.matttax.youtubedownloader.core.VideoSearcher
import com.matttax.youtubedownloader.core.config.SearchConfig
import com.matttax.youtubedownloader.youtube.search.SearchException
import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata

class SearchVideosUseCase(
    private val videoSearcher: VideoSearcher
) {
    fun executeSearch(text: String, config: SearchConfig): List<YoutubeVideoMetadata> {
        if (text == RESTORE_SEARCH_RESULTS)
            return videoSearcher.loadInitial()
        return videoSearcher.search(text, config)
    }

    fun searchFurther(): List<YoutubeVideoMetadata> {
        return try {
            videoSearcher.loadMore()
        } catch (ex: SearchException.NoContinuationException) {
            emptyList()
        }
    }

    companion object {
        const val RESTORE_SEARCH_RESULTS = "@Initial@@"
    }
}
