package com.matttax.youtubedownloader.core

import com.matttax.youtubedownloader.core.model.YoutubeStreamable

interface DataExtractor {
    fun extractStreamable(videoId: String): YoutubeStreamable
}
