package com.matttax.youtubedownloader.player.model

import com.matttax.youtubedownloader.core.model.YoutubeAudioQuality
import com.matttax.youtubedownloader.core.model.YoutubeVideoQuality

data class MediaStreamingOptions(
    val video: Map<YoutubeVideoQuality, List<MediaItemLink>>,
    val audio: Map<YoutubeAudioQuality, List<MediaItemLink>>
)
