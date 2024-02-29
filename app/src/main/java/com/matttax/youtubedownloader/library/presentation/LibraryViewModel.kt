package com.matttax.youtubedownloader.library.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.matttax.youtubedownloader.core.shift
import com.matttax.youtubedownloader.library.presentation.diff.DiffCounter
import com.matttax.youtubedownloader.library.presentation.diff.ListDiff
import com.matttax.youtubedownloader.library.repositories.model.MediaItem
import com.matttax.youtubedownloader.library.repositories.model.Playlist
import com.matttax.youtubedownloader.library.repositories.MediaRepository
import com.matttax.youtubedownloader.library.repositories.PlaylistRepository
import com.matttax.youtubedownloader.player.PlayerDelegate
import com.matttax.youtubedownloader.player.PlayerDelegateProvider
import com.matttax.youtubedownloader.player.model.PlayerMediaMetadata
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val playlistRepository: PlaylistRepository,
    private val playerDelegateProvider: PlayerDelegateProvider,
) : ViewModel() {

    val playlists = playlistRepository.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _mediaList = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaList = _mediaList.asStateFlow()

    private val _isMediaItemSelected = MutableStateFlow(false)
    val isMediaItemSelected = _isMediaItemSelected.asStateFlow()

    private val listEventChannel = Channel<ListDiff>()
    val listEventFlow = listEventChannel.receiveAsFlow()
        .onStart { emit(ListDiff.SignificantDifference) }

    private val _playlistDeletionOptions = MutableStateFlow(PlaylistDeletionOptions.NONE)
    val playlistDeletionOptions = _playlistDeletionOptions.asStateFlow()

    private val selectedPlaylists =
        Collections.synchronizedMap(HashMap<Int, MutableStateFlow<Boolean>>())

    private val movableMediaItemId = MutableStateFlow<Long?>(null)
    private val selectedPlaylistId = MutableStateFlow<Int?>(null)
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    private val delegateInited = playerDelegateProvider.isInited

    val playlistName = MutableStateFlow(UNCATEGORIZED_MEDIA_PLAYLIST_NAME)

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentPlayingUri = delegateInited
        .flatMapLatest {
            playerDelegate?.getCurrentPlayingUri() ?: flow { emit(null) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val isPlaying = delegateInited
        .flatMapLatest {
            playerDelegate?.getIsPlaying() ?: flow { emit(false) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        observeMedia()
        observePlaylistName()
        observeMediaItemPlaylists()
        refreshTrigger.tryEmit(Unit)
        mediaList.combine(
            isPlaying
        ) { list, isPlaying ->
            if (list.isNotEmpty() && isPlaying && !_isMediaItemSelected.value) {
                _isMediaItemSelected.value = true
                playerDelegate?.getQueue()?.let { queue ->
                    forceOrderItems(queue)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getExoInstance(): Player {
        return playerDelegate?.getPlayerInstance()!!
    }

    fun onSetItem(itemPosition: Int) {
        _isMediaItemSelected.value = true
        playerDelegate?.play(
            playlist = _mediaList.value.map { it.toPlayerMediaMetadata() },
            startPosition = itemPosition,
            shuffled = false,
            savePosition = false,
            format = null
        )
    }

    fun onPlayShuffled() {
        _isMediaItemSelected.value = true
        playerDelegate?.apply {
            play(
                playlist = _mediaList.value.map { it.toPlayerMediaMetadata() },
                startPosition = 0,
                shuffled = true,
                savePosition = false,
                format = null
            )
            getQueue()?.let { forceOrderItems(it) }
        }
    }

    fun onStopPlayback() {
        _isMediaItemSelected.value = false
        playerDelegate?.pause()
    }

    fun onAddPlaylist(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.addPlaylist(preprocessPlaylistName(name))
        }
    }

    fun onChoosePlaylist(id: Int?) {
        selectedPlaylistId.value = id
    }

    fun onRenameCurrentPlaylist(newName: String) {
        selectedPlaylistId.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                playlistRepository.renamePlaylist(it, preprocessPlaylistName(newName))
            }
        }
    }

    fun onDeleteItem(mediaItem: MediaItem) = viewModelScope.launch(Dispatchers.IO) {
        mediaRepository.deleteByPath(mediaItem.path)
        val file = File(mediaItem.path)
        val thumbnail = File(mediaItem.thumbnailUri)
        file.delete()
        thumbnail.delete()
    }

    fun onEditItem(position: Int, newTitle: String, newAuthor: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _mediaList.value[position].id?.let { id ->
                mediaRepository.editMediaItemById(id, newTitle, newAuthor)
            }
        }
    }

    fun getMediaItemPlaylists(id: Long?) {
        movableMediaItemId.value = id
    }

    fun onSelectPlaylist(id: Int, state: Boolean) {
        getMutablePlaylistSelected(id).value = state
    }

    fun onRemoveCurrentPlaylist() {
        selectedPlaylistId.value?.let {
            selectedPlaylistId.value = null
            if (_playlistDeletionOptions.value == PlaylistDeletionOptions.NONE)
                _playlistDeletionOptions.value = PlaylistDeletionOptions.JUST_PLAYLIST
            viewModelScope.launch(Dispatchers.IO) {
                when (_playlistDeletionOptions.value) {
                    PlaylistDeletionOptions.JUST_PLAYLIST -> playlistRepository.removePlaylist(it)
                    PlaylistDeletionOptions.PLAYLIST_WITH_ITEMS ->
                        playlistRepository.removePlaylist(id = it, withItems = true)
                    PlaylistDeletionOptions.NONE -> { }
                }
                _playlistDeletionOptions.value = PlaylistDeletionOptions.NONE
            }
        }
    }

    fun onPlaylistDeletionOptionsChanged(removeItems: Boolean) {
        _playlistDeletionOptions.value = when (removeItems) {
            true -> PlaylistDeletionOptions.PLAYLIST_WITH_ITEMS
            false -> PlaylistDeletionOptions.JUST_PLAYLIST
        }
    }

    fun getPlaylistSelectionState(id: Int): StateFlow<Boolean> {
        return getMutablePlaylistSelected(id).asStateFlow()
    }

    fun onAddMediaItemToSelectedPlaylists(mediaId: Long) {
        val playlistIds = selectedPlaylists.keys
            .filter { selectedPlaylists[it]?.value == true }
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepository.addMediaItemToPlaylists(mediaId, playlistIds)
            refreshTrigger.emit(Unit)
        }
    }

    fun onItemsShifted(from: Int, to: Int) {
        updateMediaList { shift(from, to) }
        viewModelScope.launch { listEventChannel.send(ListDiff.NoDifference) }
        playerDelegate?.shiftItemInQueue(from, to)
    }

    fun onPausePlayback() = playerDelegate?.pause()

    fun onResumePlayback() = playerDelegate?.resume()

    private fun forceOrderItems(newOrder: List<Int>) {
        val oldList = _mediaList.value
        val newList = newOrder.associateBy { oldList[it] }.keys.toList()
        _mediaList.value = newList
        refreshTrigger.tryEmit(Unit)
    }

    private fun getMutablePlaylistSelected(id: Int): MutableStateFlow<Boolean> {
        return selectedPlaylists[id] ?: run {
            val newFlow = MutableStateFlow(false)
            selectedPlaylists[id] = newFlow
            newFlow
        }
    }

    private fun updateMediaList(operation: MutableList<MediaItem>.() -> Unit) {
        _mediaList.update {
            it.toMutableList().apply { operation() }
        }
    }

    private fun handleListChange(newList: List<MediaItem>) {
        viewModelScope.launch(Dispatchers.IO) {
            val listDiff = DiffCounter(
                _mediaList.value, newList
            ) { item -> item.id ?: 0 }.countListDiff()
            when (listDiff) {
                is ListDiff.ItemInserted -> updateMediaList { add(0, newList.first()) }
                is ListDiff.ItemDeleted -> updateMediaList { removeAt(listDiff.position) }
                is ListDiff.ItemModified -> updateMediaList {
                    set(
                        listDiff.oldListPosition,
                        newList[listDiff.newListPosition]
                    )
                }
                is ListDiff.SignificantDifference -> {
                    _mediaList.value = newList
                }
                is ListDiff.NoDifference -> {}
            }
            listEventChannel.send(listDiff)
        }
    }

    private fun preprocessPlaylistName(newName: String): String {
        return if (newName == UNCATEGORIZED_MEDIA_PLAYLIST_NAME) {
            "$newName 1"
        } else newName
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeMedia() {
        selectedPlaylistId
            .combine(refreshTrigger) { playlist, _ ->
                playlist
            }
            .flatMapLatest {
                when(it) {
                    null -> mediaRepository.getAllMedia()
                    else -> mediaRepository.getAllFromPlaylist(it)
                }
            }.onEach {
                handleListChange(it)
            }.launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observePlaylistName() {
        selectedPlaylistId
            .flatMapLatest {
                if (it == null) {
                    flow<Playlist?> { emit(null) }
                } else {
                    playlistRepository.getPlaylistById(it)
                }
            }.onEach {
                playlistName.value = it?.name ?: UNCATEGORIZED_MEDIA_PLAYLIST_NAME
            }.launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeMediaItemPlaylists() {
        movableMediaItemId
            .flatMapLatest {
                when(it) {
                    null -> flow<List<Int>?> { emit(null) }
                    else -> mediaRepository.getMediaItemPlaylistsById(it)
                }
            }.onEach {
                it?.forEach { id ->
                    if (selectedPlaylists.keys.contains(id)) {
                        selectedPlaylists[id]?.value = true
                    } else {
                        selectedPlaylists[id] = MutableStateFlow(true)
                    }
                } ?: run {
                    selectedPlaylists.clear()
                }
            }.launchIn(viewModelScope)
    }

    private val playerDelegate: PlayerDelegate?
        get() = playerDelegateProvider.playerDelegate

    companion object {
        const val UNCATEGORIZED_MEDIA_PLAYLIST_NAME = "All media"
    }
}

fun MediaItem.toPlayerMediaMetadata() = PlayerMediaMetadata(
    title = title,
    author = author,
    thumbnailUri = thumbnailUri,
    contentUri = path
)
