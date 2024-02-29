package com.matttax.youtubedownloader.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerDelegateProvider @Inject constructor(
    @ApplicationContext context: Context
) {

    private val _isInited = MutableStateFlow(false)
    val isInited = _isInited.asStateFlow()

    private var _player: Player? = null

    init {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener(
            { _player = controllerFuture.get() },
            MoreExecutors.directExecutor()
        )
    }

    @Volatile
    var playerDelegate: PlayerDelegate? = null
        set(value) {
            field = value
            _isInited.value = value != null
        }
}
