package com.matttax.youtubedownloader.library.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.matttax.youtubedownloader.library.repositories.model.MediaItem
import com.matttax.youtubedownloader.library.repositories.model.Playlist
import com.matttax.youtubedownloader.library.repositories.MediaRepository
import com.matttax.youtubedownloader.library.repositories.PlaylistRepository
import com.matttax.youtubedownloader.player.PlayerDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val playlistRepository: PlaylistRepository,
    private val playerDelegate: PlayerDelegate,
) : ViewModel() {

    private val _mediaList = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaList = _mediaList.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists = _mediaList.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    init {
        mediaRepository.getAllMedia()
            .onEach { _mediaList.value = it }
            .launchIn(viewModelScope)
        playlistRepository.getAllPlaylists()
            .onEach { _playlists.value = it }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        playerDelegate.release()
    }

    fun getExoInstance(): ExoPlayer {
        return playerDelegate.exoPlayer
    }

    fun onSetItem(itemPosition: Int) {
        _isPlaying.value = true
        val uris = _mediaList.value.map { it.path }
        playerDelegate.play(uris, itemPosition)
    }

    fun onStopPlayback() {
        _isPlaying.value = false
        playerDelegate.pause()
        playerDelegate.clear()
    }

    fun onAddRepository(name: String) {
        playlistRepository.addPlaylist(name)
    }
}
