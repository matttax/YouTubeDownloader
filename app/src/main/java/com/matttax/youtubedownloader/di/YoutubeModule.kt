package com.matttax.youtubedownloader.di

import com.github.kiulian.downloader.YoutubeDownloader
import com.matttax.youtubedownloader.domain.DataExtractor
import com.matttax.youtubedownloader.domain.VideoSearcher
import com.matttax.youtubedownloader.domain.usecases.ExtractDataUseCase
import com.matttax.youtubedownloader.domain.usecases.SearchVideosUseCase
import com.matttax.youtubedownloader.youtube.DataExtractorImpl
import com.matttax.youtubedownloader.youtube.VideoSearcherImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object YoutubeModule {

    @Provides
    @ViewModelScoped
    fun provideYoutubeDownloader(): YoutubeDownloader {
        return YoutubeDownloader()
    }

    @Provides
    @ViewModelScoped
    fun provideVideoSearcher(youtubeDownloader: YoutubeDownloader): VideoSearcherImpl {
        return VideoSearcherImpl(youtubeDownloader)
    }

    @Provides
    @ViewModelScoped
    fun provideDataExtractor(youtubeDownloader: YoutubeDownloader): DataExtractorImpl {
        return DataExtractorImpl(youtubeDownloader)
    }

    @Provides
    @ViewModelScoped
    fun provideSearchVideoUseCase(videoSearcher: VideoSearcher): SearchVideosUseCase {
        return SearchVideosUseCase(videoSearcher)
    }

    @Provides
    @ViewModelScoped
    fun provideExtractDataUseCase(dataExtractor: DataExtractor): ExtractDataUseCase {
        return ExtractDataUseCase(dataExtractor)
    }
}