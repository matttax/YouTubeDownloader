package com.matttax.youtubedownloader.di

import android.content.Context
import androidx.room.Room
import com.matttax.youtubedownloader.library.datasource.MediaDatabase
import com.matttax.youtubedownloader.library.repositories.MediaRepository
import com.matttax.youtubedownloader.library.repositories.PlaylistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LibraryModule {

    @Provides
    @Singleton
    fun provideMediaDatabase(@ApplicationContext context: Context): MediaDatabase {
        return Room
            .databaseBuilder(
                context = context,
                klass = MediaDatabase::class.java,
                name = "youtube_media."
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMediaRepository(mediaDatabase: MediaDatabase): MediaRepository {
        return MediaRepository(mediaDatabase)
    }

    @Provides
    @Singleton
    fun providePlaylistRepository(mediaDatabase: MediaDatabase): PlaylistRepository {
        return PlaylistRepository(mediaDatabase)
    }
}
