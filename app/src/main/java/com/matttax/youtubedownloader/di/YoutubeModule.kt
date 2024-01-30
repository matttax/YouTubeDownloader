package com.matttax.youtubedownloader.di

import com.github.kiulian.downloader.YoutubeDownloader
import com.matttax.youtubedownloader.core.DataExtractor
import com.matttax.youtubedownloader.core.VideoSearcher
import com.matttax.youtubedownloader.youtube.usecases.ExtractDataUseCase
import com.matttax.youtubedownloader.youtube.usecases.SearchVideosUseCase
import com.matttax.youtubedownloader.youtube.DataExtractorImpl
import com.matttax.youtubedownloader.youtube.search.SearchCache
import com.matttax.youtubedownloader.youtube.search.VideoSearcherImpl
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
    fun provideVideoSearcher(
        youtubeDownloader: YoutubeDownloader,
        searchCache: SearchCache
    ): VideoSearcherImpl {
        return VideoSearcherImpl(youtubeDownloader, searchCache)
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
    fun provideExtractDataUseCase(dataExtractor: DataExtractor, searchCache: SearchCache): ExtractDataUseCase {
        return ExtractDataUseCase(dataExtractor, searchCache)
    }
}
