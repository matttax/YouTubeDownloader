package com.matttax.youtubedownloader.di

import android.app.Application
import androidx.media3.exoplayer.ExoPlayer
import com.matttax.youtubedownloader.player.PlayerDelegate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object VideoPlayerModule {

    @Provides
    @ViewModelScoped
    fun provideExoPlayer(app: Application): ExoPlayer {
        return ExoPlayer.Builder(app).build()
    }

    @Provides
    @ViewModelScoped
    fun providePlayerDelegate(exoPlayer: ExoPlayer): PlayerDelegate {
        return PlayerDelegate(exoPlayer)
    }
}
