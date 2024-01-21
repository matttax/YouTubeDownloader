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

    fun onSetPlayWhenQuit(state: Boolean) {
        settingsManager.setPlaybackStopWhenQuit(state)
        _playerSettings.update { it.copy(stopWhenQuit = state) }
    }

    fun onChangeSearchOption(option: SearchOptions, state: Boolean) {
        when(option) {
            SearchOptions.SHOW_LIVES -> {
                settingsManager.setShowLives(state)
                _searchSettings.update { it.copy(showLives = state) }
            }
            SearchOptions.SHOW_SHORTS -> {
                settingsManager.setShowShorts(state)
                _searchSettings.update { it.copy(showShorts = state) }
            }
            SearchOptions.AUTOCORRECT -> {
                settingsManager.setSearchAutocorrection(state)
                _searchSettings.update { it.copy(isAutocorrectionOn = state) }
            }
        }
    }
}
