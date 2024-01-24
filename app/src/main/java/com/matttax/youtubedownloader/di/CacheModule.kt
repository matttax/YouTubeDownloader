package com.matttax.youtubedownloader.di

import com.matttax.youtubedownloader.youtube.search.SearchCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CacheModule {

    @Provides
    fun provideSearchCache(): SearchCache {
        return SearchCache()
    }
}
