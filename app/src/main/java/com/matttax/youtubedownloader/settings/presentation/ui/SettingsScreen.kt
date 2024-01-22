package com.matttax.youtubedownloader.settings.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.matttax.youtubedownloader.settings.model.SearchOptions
import com.matttax.youtubedownloader.settings.presentation.SettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

@Composable
fun SettingsScreen(
    modifier: Modifier,
    viewModel: SettingsViewModel
) {
    Column(
        modifier = modifier
    ) {
        SettingsBlock(
            text = "Appearance"
        ) {
            CheckboxOption(
                text = "Dark theme",
                checkedState = MutableStateFlow(false)
            ) {}
        }
        SettingsBlock(
            text = "Player"
        ) {
            CheckboxOption(
                text = "Stop playback when quit",
                checkedState = viewModel.playerSettings.map { it.stopWhenHidden }
            ) {
                viewModel.onSetPlayWhenHidden(it)
            }
        }
        SettingsBlock(
            text = "Search"
        ) {
            CheckboxOption(
                text = "Autocorrection",
                checkedState = viewModel.searchSettings.map { it.isAutocorrectionOn }
            ) {
                viewModel.onChangeSearchOption(SearchOptions.AUTOCORRECT, it)
            }
            CheckboxOption(
                text = "Show movies",
                checkedState = viewModel.searchSettings.map { it.showMovies }
            ) {
                viewModel.onChangeSearchOption(SearchOptions.SHOW_MOVIES, it)
            }
            CheckboxOption(
                text = "Show lives",
                checkedState = viewModel.searchSettings.map { it.showLives }
            ) {
                viewModel.onChangeSearchOption(SearchOptions.SHOW_LIVES, it)
            }
        }
    }
}
