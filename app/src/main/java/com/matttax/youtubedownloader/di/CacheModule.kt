package com.matttax.youtubedownloader.di

import android.content.Context
import com.google.gson.Gson
import com.matttax.youtubedownloader.youtube.search.HistoryCacheManager
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
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideHistoryCacheManager(
        @ApplicationContext applicationContext: Context,
        gson: Gson
    ): HistoryCacheManager {
        return HistoryCacheManager(applicationContext, gson)
    }

    @Provides
    @Singleton
    fun provideSearchCache(historyCacheManager: HistoryCacheManager): SearchCache {
        return SearchCache(historyCacheManager)
    }
}
