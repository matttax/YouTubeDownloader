package com.matttax.youtubedownloader.domain

import com.matttax.youtubedownloader.domain.model.YoutubeStreamable

interface DataExtractor {
    fun extractStreamable(videoId: String): YoutubeStreamable
}
