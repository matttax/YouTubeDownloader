package com.matttax.youtubedownloader.youtube.presentation.ui.utils

import com.matttax.youtubedownloader.core.config.Duration
import com.matttax.youtubedownloader.core.config.SortedBy
import com.matttax.youtubedownloader.core.config.Uploaded

object FiltersConsts {
    val SORTING = Filter(
        name = "Sort by",
        argumentMap = enumValues<SortedBy>().associateBy { it.text },
    )
    val DURATION = Filter(
        name = "Sort by",
        argumentMap = enumValues<Duration>().associateBy { it.text }
    )
    val UPLOADED = Filter(
        name = "Uploaded",
        argumentMap = enumValues<Uploaded>().associateBy { it.text }
    )
}

data class Filter<T>(
    val name: String,
    val argumentMap: Map<String, T>,
)
