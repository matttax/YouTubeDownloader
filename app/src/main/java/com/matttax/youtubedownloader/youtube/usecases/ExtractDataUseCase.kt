package com.matttax.youtubedownloader.youtube.usecases

import com.matttax.youtubedownloader.core.DataExtractor
import com.matttax.youtubedownloader.youtube.search.SearchCache

class ExtractDataUseCase(
    private val dataExtractor: DataExtractor,
    private val searchCache: SearchCache
) {
    operator fun invoke(id: String) =
        searchCache.getStreamableData(id) ?: dataExtractor.extractStreamable(id)
            .also { result -> searchCache.putStreamableData(id, result) }
}
