package com.matttax.youtubedownloader.youtube.mappers

import com.matttax.youtubedownloader.core.model.MediaFormat
import com.matttax.youtubedownloader.core.model.YoutubeAudioQuality
import com.matttax.youtubedownloader.core.model.YoutubeVideoQuality

object SelectionMapper {

    val FORMAT_MAP = enumValues<MediaFormat>().associateBy { it.text }

    val VIDEO_QUALITY_MAP = enumValues<YoutubeVideoQuality>().associateBy { "${it.pixels}p" }

    val AUDIO_QUALITY_MAP = enumValues<YoutubeAudioQuality>().associateBy { it.text }
}
