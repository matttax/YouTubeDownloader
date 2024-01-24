package com.matttax.youtubedownloader.youtube.presentation.states

import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata

sealed class YoutubeSearchState {
    data class Results(val videoList: List<YoutubeVideoMetadata>): YoutubeSearchState()
    object NetworkError: YoutubeSearchState()
    object Loading: YoutubeSearchState()
}
