package com.matttax.youtubedownloader.domain.usecases

import com.matttax.youtubedownloader.domain.DataExtractor

class ExtractDataUseCase(
    private val dataExtractor: DataExtractor
) {
    operator fun invoke(id: String) = dataExtractor.extractStreamable(id)
}
