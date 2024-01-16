package com.matttax.youtubedownloader.youtube.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.matttax.youtubedownloader.core.config.Duration
import com.matttax.youtubedownloader.core.config.SearchConfig
import com.matttax.youtubedownloader.core.config.SortedBy
import com.matttax.youtubedownloader.core.config.Uploaded
import com.matttax.youtubedownloader.core.model.*
import com.matttax.youtubedownloader.player.PlayerDelegate
import com.matttax.youtubedownloader.youtube.mappers.getStreamingOptions
import com.matttax.youtubedownloader.youtube.usecases.ExtractDataUseCase
import com.matttax.youtubedownloader.youtube.usecases.SearchVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchVideosUseCase: SearchVideosUseCase,
    private val extractDataUseCase: ExtractDataUseCase,
    private val player: PlayerDelegate
) : ViewModel() {

    private var lastSearchedText: String? = null

    private val _videoList = MutableStateFlow<List<YoutubeVideoMetadata>>(emptyList())
    private val _currentStreamable = MutableStateFlow<YoutubeStreamable?>(null)
    private val _searchText = MutableStateFlow("")
    private val _isSearching = MutableStateFlow(false)
    private val _isLoadingPage = MutableStateFlow(false)
    private val _searchConfig = MutableStateFlow(SearchConfig())
    private val _uriSelectionState = MutableStateFlow<UriSelectionState?>(null)

    val videoList = _videoList.asStateFlow()
    val searchText = _searchText.asStateFlow()
    val isSearching = _isSearching.asStateFlow()
    val isLoadingPage = _isLoadingPage.asStateFlow()
    val currentStreamable = _currentStreamable.asStateFlow()
    val searchConfig = _searchConfig.asStateFlow()
    val uriSelectionState = _uriSelectionState.asStateFlow()

    init {
        onSearch("")
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }

    fun getExoInstance(): ExoPlayer {
        return player.exoPlayer
    }

    fun onSearch(text: String) {
        player.clear()
        lastSearchedText = text
        _currentStreamable.value = null
        viewModelScope.launch {
            _isSearching.value = true
            _videoList.value = withContext(Dispatchers.IO) {
                searchVideosUseCase.executeSearch(text, _searchConfig.value)
            }
            _isSearching.value = false
        }
    }

    fun onNextPage() = viewModelScope.launch {
        _isLoadingPage.value = true
        if (_searchText.value != lastSearchedText) return@launch
        _videoList.value = withContext(Dispatchers.IO) {
            _videoList.value + searchVideosUseCase.searchFurther()
        }
        _isLoadingPage.value = false
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
                player.setStreamingOptions(it.getStreamingOptions())
                player.play(newFormat)
            } catch (noElement: NoSuchElementException) {

            }
        }
        _uriSelectionState.value = getInitialSelectionState()
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
        player.play(
            format = when(newMediaFormat) {
                MediaFormat.AUDIO -> _currentStreamable.value!!.audioFormats.first()
                MediaFormat.VIDEO -> _currentStreamable.value!!.videoFormats.first()
            },
            savePosition = true
        ) //TODO()
        _uriSelectionState.value = getInitialSelectionState(newMediaFormat)
    }

    fun onMimeTypeChanged(mimeType: String) {
        val streamableStable = _currentStreamable.value ?: return
        val currentPlaying = player.playing ?: return
        val newFormat = when(currentPlaying) {
            is Format.Audio ->
                streamableStable.audioFormats.first { it.mimeType == mimeType && currentPlaying.quality == it.quality }
            is Format.Video ->
                streamableStable.videoFormats.first { it.mimeType == mimeType && currentPlaying.quality == it.quality }
        } //TODO()
        player.play(format = newFormat, savePosition = true)
        _uriSelectionState.update {
            it?.copy(
                selectedMime = mimeType,
            )
        }
    }

    fun <Q> onQualityChanged(newQuality: Q) {
        when(newQuality) {
            is YoutubeAudioQuality -> onAudioQualityChanged(newQuality)
            is YoutubeVideoQuality -> onVideoQualityChanged(newQuality)
            else -> throw Exception() //TODO()
        }
    }

    private fun onVideoQualityChanged(newQuality: YoutubeVideoQuality) {
        val streamableStable = _currentStreamable.value ?: return
        val newFormat = streamableStable.videoFormats.first { it.quality == newQuality } //TODO()
        player.play(newFormat, savePosition = true)
        _uriSelectionState.value = _uriSelectionState.value?.copy(
            selectedQuality = "${newQuality.pixels}p",
            selectedMime = player.playing?.mimeType ?: "",
            mimeOptions = getMimeOptionsVideo(newQuality)
        )
    }

    private fun onAudioQualityChanged(newQuality: YoutubeAudioQuality) {
        val streamableStable = _currentStreamable.value ?: return
        val newFormat = streamableStable.audioFormats.first { it.quality == newQuality } //TODO()
        player.play(newFormat, savePosition = true)
        _uriSelectionState.value = _uriSelectionState.value?.copy(
            selectedQuality = newQuality.text,
            selectedMime = player.playing?.mimeType ?: "",
            mimeOptions = getMimeOptionsAudio(newQuality)
        )
    }

    private fun getInitialSelectionState(mediaFormat: MediaFormat? = null): UriSelectionState {
        val currentOptions = player.streamingOptions
        val currentPlaying = player.playing
        if (
            currentOptions == null
            || currentPlaying == null
            || (currentOptions.video.isEmpty() && currentOptions.audio.isEmpty())
        ) {
            throw Exception() //TODO()
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
        return player.streamingOptions?.let {
            when(mediaFormat) {
                MediaFormat.VIDEO -> it.video.keys.map { quality -> "${quality.pixels}p" }
                MediaFormat.AUDIO -> it.audio.keys.map { quality -> quality.text }
            }
        } ?: throw Exception() //TODO()
    }

    private fun getMimeOptionsAudio(selectedQuality: YoutubeAudioQuality): List<String> {
        return player.streamingOptions?.audio?.get(selectedQuality)?.map { it.mimeType } ?: emptyList()
    }

    private fun getMimeOptionsVideo(selectedQuality: YoutubeVideoQuality): List<String> {
        return player.streamingOptions?.video?.get(selectedQuality)?.map { it.mimeType } ?: emptyList()
    }
}
