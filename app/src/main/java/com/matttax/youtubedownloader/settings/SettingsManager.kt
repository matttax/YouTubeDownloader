package com.matttax.youtubedownloader.settings

import android.content.Context
import com.matttax.youtubedownloader.settings.model.PlayerSettings
import com.matttax.youtubedownloader.settings.model.SearchSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext val context: Context
) {

    private val sharedPrefs = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

    fun getSearchSettings(): SearchSettings {
        val autocorrection = sharedPrefs.getBoolean(SEARCH_SETTINGS_AUTOCORRECTION, false)
        val shorts = sharedPrefs.getBoolean(SEARCH_SETTINGS_SHORTS, true)
        val lives = sharedPrefs.getBoolean(SEARCH_SETTINGS_LIVES, true)
        return SearchSettings(autocorrection, shorts, lives)
    }

    fun getPlayerSettings(): PlayerSettings {
        val pauseWhenQuit = sharedPrefs.getBoolean(PLAYER_SETTINGS_QUIT_PAUSE, true)
        return PlayerSettings(pauseWhenQuit)
    }

    fun setPlaybackStopWhenQuit(state: Boolean) {
        sharedPrefs.edit().putBoolean(PLAYER_SETTINGS_QUIT_PAUSE, state).apply()
    }

    fun setSearchAutocorrection(state: Boolean) {
        sharedPrefs.edit().putBoolean(SEARCH_SETTINGS_AUTOCORRECTION, state).apply()
    }

    fun setShowLives(state: Boolean) {
        sharedPrefs.edit().putBoolean(SEARCH_SETTINGS_LIVES, state).apply()
    }

    fun setShowShorts(state: Boolean) {
        sharedPrefs.edit().putBoolean(SEARCH_SETTINGS_SHORTS, state).apply()
    }

    companion object {
        const val APP_PREFERENCES = "settings"
        const val SEARCH_SETTINGS_AUTOCORRECTION = "autocorrection_on"
        const val SEARCH_SETTINGS_SHORTS = "show_shorts"
        const val SEARCH_SETTINGS_LIVES = "show_lives"
        const val PLAYER_SETTINGS_QUIT_PAUSE = "quit_pause"
    }
}
