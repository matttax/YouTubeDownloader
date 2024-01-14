package com.matttax.youtubedownloader.youtube.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.matttax.youtubedownloader.core.config.Duration
import com.matttax.youtubedownloader.core.config.SearchConfig
import com.matttax.youtubedownloader.core.config.SortedBy
import com.matttax.youtubedownloader.core.config.Uploaded
import com.matttax.youtubedownloader.core.model.*
import com.matttax.youtubedownloader.youtube.download.model.MediaStreamingOptions
import com.matttax.youtubedownloader.youtube.mappers.getVideoDownloadOptions
import com.matttax.youtubedownloader.youtube.usecases.ExtractDataUseCase
import com.matttax.youtubedownloader.youtube.usecases.SearchVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchVideosUseCase: SearchVideosUseCase,
    private val extractDataUseCase: ExtractDataUseCase,
    val exoPlayer: ExoPlayer
) : ViewModel() {

    private var lastSearchedText: String? = null
    private var streamingOptions: MediaStreamingOptions? = null
    private var playing: Format? = null

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
        exoPlayer.prepare()
        onSearch("")
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }

    fun onSearch(text: String) {
        lastSearchedText = text
        exoPlayer.clearMediaItems()
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
                streamingOptions = it.getVideoDownloadOptions()
                playing = (
                        if (it.videoFormats.isNotEmpty())
                            it.videoFormats.first()
                        else if (it.audioFormats.isNotEmpty())
                            it.audioFormats.first()
                        else throw NoSuchElementException()
                        )
                    .also {
                        format -> exoPlayer.setMediaItem(MediaItem.fromUri(format.url))
                    }
                exoPlayer.play()
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
        playing = when(newMediaFormat) {
            MediaFormat.AUDIO -> _currentStreamable.value!!.audioFormats.first()
            MediaFormat.VIDEO -> _currentStreamable.value!!.videoFormats.first()
        }
        _uriSelectionState.value = getInitialSelectionState(newMediaFormat)
        updateMedia()
    }

    fun <Q> onQualityChanged(newQuality: Q) {
        when(newQuality) {
            is YoutubeAudioQuality -> onAudioQualityChanged(newQuality)
            is YoutubeVideoQuality -> onVideoQualityChanged(newQuality)
            else -> throw Exception() //TODO()
        }
        updateMedia()
    }

    fun onMimeTypeChanged(mimeType: String) {
        val streamableStable = _currentStreamable.value ?: return
        val playingStable = playing
        _uriSelectionState.value = _uriSelectionState.value?.copy(
            selectedMime = mimeType,
        )
        playing = when(playingStable) {
            is Format.Audio ->
                streamableStable.audioFormats.first { it.mimeType == mimeType && playingStable.quality == it.quality }
            is Format.Video ->
                streamableStable.videoFormats.first { it.mimeType == mimeType && playingStable.quality == it.quality }
            null -> throw Exception() // TODO()
        }
        updateMedia()
    }

    private fun onVideoQualityChanged(newQuality: YoutubeVideoQuality) {
        val streamableStable = _currentStreamable.value ?: return
        playing = streamableStable.videoFormats.first { it.quality == newQuality }
        _uriSelectionState.value = _uriSelectionState.value?.copy(
            selectedQuality = "${newQuality.pixels}p",
            selectedMime = playing?.mimeType ?: "",
            mimeOptions = streamingOptions?.video?.get(newQuality)?.map { it.mimeType } ?: emptyList()
        )
    }

    private fun onAudioQualityChanged(newQuality: YoutubeAudioQuality) {
        val streamableStable = _currentStreamable.value ?: return
        playing = streamableStable.audioFormats.first { it.quality == newQuality }
        _uriSelectionState.value = _uriSelectionState.value?.copy(
            selectedQuality = newQuality.text,
            selectedMime = playing?.mimeType ?: "",
            mimeOptions = streamingOptions?.audio?.get(newQuality)?.map { it.mimeType } ?: emptyList()
        )
    }

    private fun getInitialSelectionState(mediaFormat: MediaFormat? = null): UriSelectionState {
        val downloadOptionsStable = streamingOptions
        val playingStable = playing
        if (
            downloadOptionsStable == null
            || playingStable == null
            || (downloadOptionsStable.video.isEmpty() && downloadOptionsStable.audio.isEmpty())
        ) {
            throw Exception() //TODO()
        }
        val (formatOptions, selectedFormat) = if (
            downloadOptionsStable.video.isNotEmpty() && downloadOptionsStable.audio.isNotEmpty()
        ) {
            listOf(MediaFormat.VIDEO, MediaFormat.AUDIO) to (mediaFormat ?: MediaFormat.VIDEO)
        } else if (downloadOptionsStable.video.isEmpty())  {
            listOf(MediaFormat.AUDIO) to (mediaFormat ?: MediaFormat.AUDIO)
        } else {
            listOf(MediaFormat.VIDEO) to (mediaFormat ?: MediaFormat.VIDEO)
        }
        val selectedQuality = when(playingStable) {
            is Format.Audio -> playingStable.quality.text
            is Format.Video -> "${playingStable.quality.pixels}p"
        }
        val (mimeOptions, selectedMime) = when(playingStable) {
            is Format.Audio -> getMimeOptionsAudio(playingStable.quality) to playingStable.mimeType
            is Format.Video -> getMimeOptionsVideo(playingStable.quality) to playingStable.mimeType
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
        return streamingOptions?.let {
            when(mediaFormat) {
                MediaFormat.VIDEO -> streamingOptions?.video?.keys?.map { "${it.pixels}p" }
                MediaFormat.AUDIO -> streamingOptions?.audio?.keys?.map { it.text }
            }
        } ?: throw Exception() //TODO()
    }

    private fun getMimeOptionsAudio(selectedQuality: YoutubeAudioQuality): List<String> {
        return streamingOptions?.audio?.get(selectedQuality)?.map { it.mimeType } ?: throw Exception() //TODO()
    }

    private fun getMimeOptionsVideo(selectedQuality: YoutubeVideoQuality): List<String> {
        return streamingOptions?.video?.get(selectedQuality)?.map { it.mimeType } ?: throw Exception() //TODO()
    }

    private fun updateMedia() {
        playing?.let {
            val currentPosition = exoPlayer.currentPosition
            exoPlayer.setMediaItem(MediaItem.fromUri(it.url))
            exoPlayer.seekTo(currentPosition)
            exoPlayer.play()
        }
    }
}
