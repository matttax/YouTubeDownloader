package com.matttax.youtubedownloader.di

import com.matttax.youtubedownloader.core.DataExtractor
import com.matttax.youtubedownloader.core.VideoSearcher
import com.matttax.youtubedownloader.youtube.DataExtractorImpl
import com.matttax.youtubedownloader.youtube.search.VideoSearcherImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class CoreModule {

    @Binds
    abstract fun bindVideoSearcher(videoSearcherImpl: VideoSearcherImpl): VideoSearcher

    @Binds
    abstract fun bindDataExtractor(dataExtractorImpl: DataExtractorImpl): DataExtractor
}
