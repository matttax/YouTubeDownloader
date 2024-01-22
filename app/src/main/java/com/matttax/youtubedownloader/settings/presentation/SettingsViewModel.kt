package com.matttax.youtubedownloader.settings.presentation

import androidx.lifecycle.ViewModel
import com.matttax.youtubedownloader.settings.SettingsManager
import com.matttax.youtubedownloader.settings.model.SearchOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _playerSettings = MutableStateFlow(settingsManager.getPlayerSettings())
    private val _searchSettings = MutableStateFlow(settingsManager.getSearchSettings())

    val playerSettings = _playerSettings.asStateFlow()
    val searchSettings = _searchSettings.asStateFlow()

    fun onSetPlayWhenHidden(state: Boolean) {
        settingsManager.setPlaybackStopWhenHidden(state)
        _playerSettings.update { it.copy(stopWhenHidden = state) }
    }

    fun onChangeSearchOption(option: SearchOptions, state: Boolean) {
        when(option) {
            SearchOptions.SHOW_LIVES -> {
                settingsManager.setShowLives(state)
                _searchSettings.update { it.copy(showLives = state) }
            }
            SearchOptions.SHOW_MOVIES -> {
                settingsManager.setShowMovies(state)
                _searchSettings.update { it.copy(showMovies = state) }
            }
            SearchOptions.AUTOCORRECT -> {
                settingsManager.setSearchAutocorrection(state)
                _searchSettings.update { it.copy(isAutocorrectionOn = state) }
            }
        }
    }
}
