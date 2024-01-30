package com.matttax.youtubedownloader.di

import android.content.Context
import com.matttax.youtubedownloader.youtube.search.CacheManager
import com.matttax.youtubedownloader.youtube.search.SearchCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CacheModule {

    @Provides
    @Singleton
    fun provideCacheManager(@ApplicationContext applicationContext: Context): CacheManager {
        return CacheManager(applicationContext)
    }

    @Provides
    @Singleton
    fun provideSearchCache(cacheManager: CacheManager): SearchCache {
        return SearchCache(cacheManager)
    }
}
