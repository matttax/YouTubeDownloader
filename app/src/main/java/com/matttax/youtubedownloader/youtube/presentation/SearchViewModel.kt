package com.matttax.youtubedownloader.youtube.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.matttax.youtubedownloader.core.config.Duration
import com.matttax.youtubedownloader.core.config.SearchConfig
import com.matttax.youtubedownloader.core.config.SortedBy
import com.matttax.youtubedownloader.core.config.Uploaded
import com.matttax.youtubedownloader.core.model.*
import com.matttax.youtubedownloader.library.repositories.model.MediaItem
import com.matttax.youtubedownloader.library.repositories.MediaRepository
import com.matttax.youtubedownloader.player.PlayerDelegate
import com.matttax.youtubedownloader.settings.SettingsManager
import com.matttax.youtubedownloader.settings.model.PlayerSettings
import com.matttax.youtubedownloader.settings.model.SearchSettings
import com.matttax.youtubedownloader.youtube.MediaDownloader
import com.matttax.youtubedownloader.youtube.mappers.getStreamingOptions
import com.matttax.youtubedownloader.youtube.presentation.states.DownloadState
import com.matttax.youtubedownloader.youtube.presentation.states.PagingState
import com.matttax.youtubedownloader.youtube.presentation.states.UriSelectionState
import com.matttax.youtubedownloader.youtube.presentation.states.YoutubeSearchState
import com.matttax.youtubedownloader.youtube.search.SearchException
import com.matttax.youtubedownloader.youtube.usecases.ExtractDataUseCase
import com.matttax.youtubedownloader.youtube.usecases.SearchVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.NoSuchElementException
import kotlin.collections.HashMap

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchVideosUseCase: SearchVideosUseCase,
    private val extractDataUseCase: ExtractDataUseCase,
    private val mediaDownloader: MediaDownloader,
    private val playerDelegate: PlayerDelegate,
    private val settingsManager: SettingsManager,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private var lastSearchedText: String? = null
    private var searchSettings: SearchSettings = settingsManager.getSearchSettings()
    private lateinit var playerSettings: PlayerSettings
    private val downloadingCache =
        Collections.synchronizedMap(HashMap<String, MutableStateFlow<DownloadState>>())

    private val _searchState = MutableStateFlow<YoutubeSearchState>(
        YoutubeSearchState.Results(emptyList())
    )
    private val _currentStreamable = MutableStateFlow<YoutubeStreamable?>(null)
    private val _searchText = MutableStateFlow("")
    private val _loadingPageState = MutableStateFlow(PagingState.NOTHING_TO_LOAD)
    private val _searchConfig = MutableStateFlow(
        SearchConfig(allowCorrection = searchSettings.isAutocorrectionOn)
    )
    private val _uriSelectionState = MutableStateFlow<UriSelectionState?>(null)

    val searchState = _searchState.asStateFlow()
    val searchText = _searchText.asStateFlow()
    val pagingState = _loadingPageState.asStateFlow()
    val currentStreamable = _currentStreamable.asStateFlow()
    val searchConfig = _searchConfig.asStateFlow()
    val uriSelectionState = _uriSelectionState.asStateFlow()

    val isVideoReady = playerDelegate.isVideoReady.combine(
        _currentStreamable.map { it != null }
    ) { playerReady, streamableExists -> playerReady && streamableExists }

    private val loadingErrorChanel = Channel<LoadingError>()
    val errorFlow = loadingErrorChanel.receiveAsFlow()

    init {
        onSearch(SearchVideosUseCase.RESTORE_SEARCH_RESULTS)
    }

    override fun onCleared() {
        super.onCleared()
        playerDelegate.release()
    }

    fun getExoInstance(): ExoPlayer {
        return playerDelegate.exoPlayer
    }

    fun onSearch(text: String = _searchText.value) {
        searchSettings = settingsManager.getSearchSettings()
        playerDelegate.clear()
        lastSearchedText = text
        _currentStreamable.value = null
        viewModelScope.launch {
            _searchState.value = YoutubeSearchState.Loading
            _searchState.value = try {
                withContext(Dispatchers.IO) {
                    val list = searchVideosUseCase
                        .executeSearch(text, _searchConfig.value)
                        .filterBySettings()
                    YoutubeSearchState.Results(list)
                }
            } catch (searchEx: SearchException.SearchFailedException) {
                YoutubeSearchState.NetworkError
            }
        }
    }

    fun onNextPage() {
        println("next")
        searchSettings = settingsManager.getSearchSettings()
        viewModelScope.launch {
            _loadingPageState.value = PagingState.LOADING
            try {
                _searchState.update {
                    when (it) {
                        is YoutubeSearchState.Results -> it.copy(
                            videoList = withContext(Dispatchers.IO) {
                                it.videoList + searchVideosUseCase.searchFurther()
                                    .filterBySettings()
                            }
                        )
                        else -> it
                    }
                }
                _loadingPageState.value = PagingState.NOTHING_TO_LOAD
            } catch (searchException: SearchException.SearchFailedException) {
                _loadingPageState.value = PagingState.NETWORK_ERROR
            }
        }
    }

    fun onExtractData(id: String) = viewModelScope.launch {
        _currentStreamable.value = null
        _currentStreamable.value = withContext(Dispatchers.IO) {
            extractDataUseCase(id)
        }.also {
            try {
                val newFormat = when {
                    it.videoFormats.isNotEmpty() -> it.videoFormats.first()
                    it.audioFormats.isNotEmpty() -> it.audioFormats.first()
                    else -> throw NoSuchElementException()
                }
                playerDelegate.setStreamingOptions(it.getStreamingOptions())
                playerDelegate.play(newFormat)
            } catch (noElement: NoSuchElementException) {
                val loadingError = if (it.metadata.isLive) {
                    LoadingError.NoStreamableLinkFound("Lives cannot be streamed")
                } else if (it.metadata.isMovie == true) {
                    LoadingError.NoStreamableLinkFound("Not enough rights to stream movies")
                } else LoadingError.NoStreamableLinkFound()
                loadingErrorChanel.send(loadingError)
            }
        }
        _uriSelectionState.value = getInitialSelectionState()
    }

    fun onDownload() {
        val currentMetadata = _currentStreamable.value?.metadata ?: return
        playerDelegate.playing?.let { format ->
            getMutableDownloadState(currentMetadata.id).update { state -> state.copy(isDownloading = true) }
            mediaDownloader.download(format, currentStreamable.value?.metadata?.name ?: "untitled")
                .onEach { getMutableDownloadState(currentMetadata.id).update { state -> state.copy(progress = it) } }
                .onCompletion {
                    getMutableDownloadState(currentMetadata.id).update { state ->
                        state.copy(
                            isDownloading = false,
                            isCompleted = true
                        )
                    }
                    addToRepo(currentMetadata, format)
                }
                .launchIn(viewModelScope)
        } ?: noStreamableCrash()
    }

    fun getCurrentDownloadState(): StateFlow<DownloadState> {
        val id = currentStreamable.value?.metadata?.id ?: noStreamableCrash()
        return getMutableDownloadState(id).asStateFlow()
    }

    fun onSearchTextChange(newText: String) {
        _searchText.value = newText
    }

    fun onSetSorting(sortedBy: SortedBy) {
        if (_searchConfig.value.sortBy == sortedBy) return
        _searchConfig.value = _searchConfig.value.copy(sortBy = sortedBy)
        lastSearchedText?.let { onSearch(it) }
    }

    fun onSetUploaded(uploaded: Uploaded) {
        if (_searchConfig.value.uploaded == uploaded) return
        _searchConfig.value = _searchConfig.value.copy(uploaded = uploaded)
        lastSearchedText?.let { onSearch(it) }
    }

    fun onSetDuration(duration: Duration) {
        if (_searchConfig.value.duration == duration) return
        _searchConfig.value = _searchConfig.value.copy(duration = duration)
        lastSearchedText?.let { onSearch(it) }
    }

    fun onMediaFormatChanged(newMediaFormat: MediaFormat) {
        if (newMediaFormat.text == _uriSelectionState.value?.selectedFormat
            || _uriSelectionState.value == null
            || _currentStreamable.value == null
        ) return
        try {
            playerDelegate.play(
                format = when (newMediaFormat) {
                    MediaFormat.AUDIO -> _currentStreamable.value!!.audioFormats.first()
                    MediaFormat.VIDEO -> _currentStreamable.value!!.videoFormats.first()
                },
                savePosition = true
            )
        } catch (noElement: NoSuchElementException) {
            notifyNoStreamableLink()
        }
        _uriSelectionState.value = getInitialSelectionState(newMediaFormat)
    }

    fun <Q> onQualityChanged(newQuality: Q) {
        when(newQuality) {
            is YoutubeAudioQuality -> onAudioQualityChanged(newQuality)
            is YoutubeVideoQuality -> onVideoQualityChanged(newQuality)
            else -> throw IllegalArgumentException("Not a YoutubeQuality class")
        }
    }

    fun onMimeTypeChanged(mimeType: String) {
        val streamableStable = _currentStreamable.value ?: return
        val currentPlaying = playerDelegate.playing ?: return
        try {
            val newFormat = when (currentPlaying) {
                is Format.Audio ->
                    streamableStable.audioFormats
                        .first { it.mimeType == mimeType && currentPlaying.quality == it.quality }
                is Format.Video ->
                    streamableStable.videoFormats
                        .first { it.mimeType == mimeType && currentPlaying.quality == it.quality }
            }
            playerDelegate.play(format = newFormat, savePosition = true)
        } catch (noElement: NoSuchElementException) {
            notifyNoStreamableLink()
        }
        _uriSelectionState.update {
            it?.copy(
                selectedMime = mimeType,
            )
        }
    }

    fun onPlayerHidden() {
        playerSettings = settingsManager.getPlayerSettings()
        if (playerSettings.stopWhenHidden) {
            playerDelegate.pause()
        }
    }

    fun onQuit() {
        playerDelegate.pause()
    }

    fun onStopPlaying() {
        onQuit()
        _currentStreamable.value = null
    }

    private fun onVideoQualityChanged(newQuality: YoutubeVideoQuality) {
        val streamableStable = _currentStreamable.value ?: return
        try {
            val newFormat = streamableStable.videoFormats.first { it.quality == newQuality }
            playerDelegate.play(newFormat, savePosition = true)
        } catch (ex: NoSuchElementException) {
            notifyNoStreamableLink()
        }
        _uriSelectionState.value = _uriSelectionState.value?.copy(
            selectedQuality = "${newQuality.pixels}p",
            selectedMime = playerDelegate.playing?.mimeType ?: "",
            mimeOptions = getMimeOptionsVideo(newQuality)
        )
    }

    private fun onAudioQualityChanged(newQuality: YoutubeAudioQuality) {
        val streamableStable = _currentStreamable.value ?: return
        try {
            val newFormat = streamableStable.audioFormats.first { it.quality == newQuality }
            playerDelegate.play(newFormat, savePosition = true)
        } catch (ex: NoSuchElementException) {
            notifyNoStreamableLink()
        }
        _uriSelectionState.value = _uriSelectionState.value?.copy(
            selectedQuality = newQuality.text,
            selectedMime = playerDelegate.playing?.mimeType ?: "",
            mimeOptions = getMimeOptionsAudio(newQuality)
        )
    }

    private fun getInitialSelectionState(mediaFormat: MediaFormat? = null): UriSelectionState {
        val currentOptions = playerDelegate.streamingOptions
        val currentPlaying = playerDelegate.playing
        if (
            currentOptions == null
            || currentPlaying == null
            || (currentOptions.video.isEmpty() && currentOptions.audio.isEmpty())
        ) {
            viewModelScope.launch { loadingErrorChanel.send(LoadingError.NoFormatOptionsAvailable) }
            _currentStreamable.value = null
            return UriSelectionState()
        }
        val (formatOptions, selectedFormat) = if (
            currentOptions.video.isNotEmpty() && currentOptions.audio.isNotEmpty()
        ) {
            listOf(MediaFormat.VIDEO, MediaFormat.AUDIO) to (mediaFormat ?: MediaFormat.VIDEO)
        } else if (currentOptions.video.isEmpty())  {
            listOf(MediaFormat.AUDIO) to (mediaFormat ?: MediaFormat.AUDIO)
        } else {
            listOf(MediaFormat.VIDEO) to (mediaFormat ?: MediaFormat.VIDEO)
        }
        val selectedQuality = when(currentPlaying) {
            is Format.Audio -> currentPlaying.quality.text
            is Format.Video -> "${currentPlaying.quality.pixels}p"
        }
        val (mimeOptions, selectedMime) = when(currentPlaying) {
            is Format.Audio -> getMimeOptionsAudio(currentPlaying.quality) to currentPlaying.mimeType
            is Format.Video -> getMimeOptionsVideo(currentPlaying.quality) to currentPlaying.mimeType
        }
        return UriSelectionState(
            formatOptions = formatOptions.map { it.text },
            selectedFormat = selectedFormat.text,
            qualityOptions = getQualityOptions(selectedFormat),
            selectedQuality = selectedQuality,
            mimeOptions = mimeOptions,
            selectedMime = selectedMime
        )
    }

    private fun getQualityOptions(mediaFormat: MediaFormat): List<String> {
        return playerDelegate.streamingOptions?.let {
            when(mediaFormat) {
                MediaFormat.VIDEO -> it.video.keys.map { quality -> "${quality.pixels}p" }
                MediaFormat.AUDIO -> it.audio.keys.map { quality -> quality.text }
            }
        } ?: emptyList()
    }

    private fun getMimeOptionsAudio(selectedQuality: YoutubeAudioQuality): List<String> {
        return playerDelegate.streamingOptions?.audio?.get(selectedQuality)
            ?.map { it.mimeType } ?: emptyList()
    }

    private fun getMimeOptionsVideo(selectedQuality: YoutubeVideoQuality): List<String> {
        return playerDelegate.streamingOptions?.video?.get(selectedQuality)
            ?.map { it.mimeType } ?: emptyList()
    }

    private fun getMutableDownloadState(id: String): MutableStateFlow<DownloadState> {
        return downloadingCache[id] ?: run {
            val newFlow = MutableStateFlow(DownloadState())
            downloadingCache[id] = newFlow
            newFlow
        }
    }

    private fun addToRepo(
        metadata: YoutubeVideoMetadata,
        format: Format
    ) = viewModelScope.launch(Dispatchers.IO) {
        with(metadata) {
            mediaRepository.addMediaItem(
                MediaItem(
                    title = name,
                    author = author,
                    description = description,
                    thumbnailUri = mediaDownloader.getThumbnailPath(format.url) ?: thumbnailUri,
                    hasVideo = format is Format.Video,
                    durationSeconds = durationSeconds,
                    path = mediaDownloader.getPath(format.url)
                )
            )
        }
    }

    private fun notifyNoStreamableLink() {
        viewModelScope.launch { loadingErrorChanel.send(LoadingError.NoStreamableLinkFound()) }
    }

    private fun noStreamableCrash(): Nothing {
        throw UnsupportedOperationException("No video is selected")
    }

    private fun List<YoutubeVideoMetadata>.filterBySettings(): List<YoutubeVideoMetadata> {
        return filter {
            if (!searchSettings.showLives) {
                !it.isLive
            } else true
        }.filter {
            if (!searchSettings.showMovies) {
                it.isMovie == false
            } else true
        }
    }
}
