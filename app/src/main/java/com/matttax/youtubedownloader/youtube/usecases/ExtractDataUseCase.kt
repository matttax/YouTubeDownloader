package com.matttax.youtubedownloader.youtube.usecases

import com.matttax.youtubedownloader.core.DataExtractor

class ExtractDataUseCase(
    private val dataExtractor: DataExtractor
) {
    operator fun invoke(id: String) = dataExtractor.extractStreamable(id)
}
