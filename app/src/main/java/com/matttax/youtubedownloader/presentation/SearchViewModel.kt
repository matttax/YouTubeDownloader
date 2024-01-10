package com.matttax.youtubedownloader.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.integration.compose.placeholder
import com.matttax.youtubedownloader.domain.config.Duration
import com.matttax.youtubedownloader.domain.config.SearchConfig
import com.matttax.youtubedownloader.domain.config.SortedBy
import com.matttax.youtubedownloader.domain.config.Uploaded
import com.matttax.youtubedownloader.domain.model.YoutubeStreamable
import com.matttax.youtubedownloader.domain.model.YoutubeVideoMetadata
import com.matttax.youtubedownloader.domain.usecases.ExtractDataUseCase
import com.matttax.youtubedownloader.domain.usecases.SearchVideosUseCase
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
    private val _videoList = MutableStateFlow<List<YoutubeVideoMetadata>>(emptyList())
    private val _currentStreamable = MutableStateFlow<YoutubeStreamable?>(null)
    private val _searchText = MutableStateFlow("")
    private val _isSearching = MutableStateFlow(false)
    private val _isLoadingPage = MutableStateFlow(false)
    private val _searchConfig = MutableStateFlow(SearchConfig())

    val videoList = _videoList.asStateFlow()
    val searchText = _searchText.asStateFlow()
    val isSearching = _isSearching.asStateFlow()
    val isLoadingPage = _isLoadingPage.asStateFlow()
    val currentStreamable = _currentStreamable.asStateFlow()
    val searchConfig = _searchConfig.asStateFlow()

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
                exoPlayer.setMediaItem(MediaItem.fromUri(it.videoFormats.first().url))
                exoPlayer.play()
            } catch (noElement: NoSuchElementException) {

            }
        }
    }

    fun onSearchTextChange(newText: String) {
        _searchText.value = newText
    }

    fun onSetSorting(sortedBy: SortedBy) {
        println()
        _searchConfig.value = _searchConfig.value.copy(sortBy = sortedBy)
        lastSearchedText?.let { onSearch(it) }
    }

    fun onSetUploaded(uploaded: Uploaded) {
        _searchConfig.value = _searchConfig.value.copy(uploaded = uploaded)
        lastSearchedText?.let { onSearch(it) }
    }

    fun onSetDuration(duration: Duration) {
        _searchConfig.value = _searchConfig.value.copy(duration = duration)
        lastSearchedText?.let { onSearch(it) }
    }

    fun onSetAutocorrection(allowCorrection: Boolean) {
        _searchConfig.value = _searchConfig.value.copy(allowCorrection = allowCorrection)
        lastSearchedText?.let { onSearch(it) }
    }
}
