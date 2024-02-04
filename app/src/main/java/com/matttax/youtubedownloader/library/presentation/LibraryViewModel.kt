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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val playlistRepository: PlaylistRepository,
    private val playerDelegate: PlayerDelegate,
) : ViewModel() {

    private val _mediaList = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaList = _mediaList.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists = _playlists.asStateFlow()

    private val _isMediaItemSelected = MutableStateFlow(false)
    val isMediaItemSelected = _isMediaItemSelected.asStateFlow()

    private val _playlistName = MutableStateFlow("All media")
    val playlistName = _playlistName.asStateFlow()

    val currentPlayingUri = playerDelegate.getCurrentPlayingUri()
    val isPlaying = playerDelegate.getIsPlaying()

    private val selectedPlaylists =
        Collections.synchronizedMap(HashMap<Int, MutableStateFlow<Boolean>>())

    private var queue: MutableList<Int>? = null

    init {
        loadAllMedia()
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
        _isMediaItemSelected.value = true
        val uris = _mediaList.value.map { it.path }
        playerDelegate.play(uris, itemPosition, queue)
    }

    fun onStopPlayback() {
        _isMediaItemSelected.value = false
        playerDelegate.pause()
        playerDelegate.clear()
    }

    fun onAddPlaylist(name: String) = viewModelScope.launch(Dispatchers.IO) {
        playlistRepository.addPlaylist(name)
    }

    fun onChoosePlaylist(id: Int) {
        mediaRepository
            .getAllFromPlaylist(id)
            .onEach {_mediaList.value = it }
            .launchIn(viewModelScope)
        _playlistName.value = _playlists.value.first { it.id == id }.name
    }

    fun onDeleteItem(path: String) = viewModelScope.launch(Dispatchers.IO) {
        mediaRepository.deleteByPath(path)
        val file = File(path)
        file.delete()
    }

    fun getMediaItemPlaylists(id: Long) {
        mediaRepository
            .getMediaItemPlaylistsById(id)
            .onEach {
                it.forEach { id -> selectedPlaylists[id] = MutableStateFlow(true) }
            }.launchIn(viewModelScope)
    }

    fun onSelectPlaylist(id: Int, state: Boolean) {
        getMutablePlaylistSelected(id).value = state
    }

    fun getPlaylistSelectionState(id: Int): StateFlow<Boolean> {
        return getMutablePlaylistSelected(id).asStateFlow()
    }

    fun onAddMediaItemToSelectedPlaylists(mediaId: Long) {
        val playlistIds = selectedPlaylists.keys
            .filter { selectedPlaylists[it]?.value == true }
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepository.addMediaItemToPlaylists(mediaId, playlistIds)
        }
        onDeselectPlaylists()
    }

    fun onDeselectPlaylists() {
        selectedPlaylists.clear()
    }

    fun loadAllMedia() {
        mediaRepository.getAllMedia()
            .onEach {
                _mediaList.value = it
                queue = MutableList(it.size) { index -> index }
            }
            .launchIn(viewModelScope)
        _playlistName.value = "All media"
    }

    fun onItemsSwapped(from: Int, to: Int) {
        _mediaList.update {
            it.toMutableList().apply {
                add(to, removeAt(from))
            }
        }
        queue.takeIf { _isMediaItemSelected.value }
            ?.apply { add(to, removeAt(from)) }
            ?.also {
                playerDelegate.setQueue(it)
            }
    }

    fun onPausePlayback() = playerDelegate.pause()

    fun onResumePlayback() = playerDelegate.resume()

    private fun getMutablePlaylistSelected(id: Int): MutableStateFlow<Boolean> {
        return selectedPlaylists[id] ?: run {
            val newFlow = MutableStateFlow(false)
            selectedPlaylists[id] = newFlow
            newFlow
        }
    }
}
