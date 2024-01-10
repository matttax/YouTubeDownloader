package com.matttax.youtubedownloader.domain.mappers

import com.github.kiulian.downloader.model.search.field.DurationField
import com.github.kiulian.downloader.model.search.field.SortField
import com.github.kiulian.downloader.model.search.field.UploadDateField
import com.matttax.youtubedownloader.domain.config.Duration
import com.matttax.youtubedownloader.domain.config.Duration.*
import com.matttax.youtubedownloader.domain.config.SortedBy
import com.matttax.youtubedownloader.domain.config.Uploaded
import com.matttax.youtubedownloader.domain.config.SortedBy.RATING
import com.matttax.youtubedownloader.domain.config.SortedBy.RELEVANCE
import com.matttax.youtubedownloader.domain.config.SortedBy.VIEW_COUNT
import com.matttax.youtubedownloader.domain.config.SortedBy.UPLOAD_DATE
import com.matttax.youtubedownloader.domain.config.Uploaded.THIS_YEAR
import com.matttax.youtubedownloader.domain.config.Uploaded.TODAY
import com.matttax.youtubedownloader.domain.config.Uploaded.THIS_WEEK
import com.matttax.youtubedownloader.domain.config.Uploaded.THIS_MONTH

object ConfigMapper {

    fun Duration.toYoutubeDuration() = when(this) {
        SHORT -> DurationField.UNDER_4_MINUTES
        MIDDLE -> DurationField.FROM_4_TO_20_MINUTES
        LONG -> DurationField.OVER_20_MINUTES
        ANY -> null
    }

    fun SortedBy.toYoutubeSorting() = when(this) {
        RELEVANCE -> SortField.RELEVANCE
        VIEW_COUNT -> SortField.VIEW_COUNT
        RATING -> SortField.RATING
        UPLOAD_DATE -> SortField.UPLOAD_DATE
    }

    fun Uploaded.toYoutubeUploaded() = when(this) {
        TODAY -> UploadDateField.DAY
        THIS_WEEK -> UploadDateField.WEEK
        THIS_MONTH -> UploadDateField.MONTH
        THIS_YEAR -> UploadDateField.YEAR
        Uploaded.ANY -> null
    }

}
