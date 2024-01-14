package com.matttax.youtubedownloader.youtube.download.model

import com.matttax.youtubedownloader.core.model.YoutubeAudioQuality
import com.matttax.youtubedownloader.core.model.YoutubeVideoQuality

data class MediaStreamingOptions(
    val video: Map<YoutubeVideoQuality, List<DownloadLink>>,
    val audio: Map<YoutubeAudioQuality, List<DownloadLink>>
)
