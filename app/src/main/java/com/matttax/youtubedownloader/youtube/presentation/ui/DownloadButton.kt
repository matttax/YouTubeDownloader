package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.matttax.youtubedownloader.core.ui.theme.LeafGreen
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed
import com.matttax.youtubedownloader.youtube.presentation.states.DownloadState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DownloadButton(
    downloading: StateFlow<DownloadState>,
    action: () -> Unit
) {
    val downloadState by downloading.collectAsState()
    Text(
        modifier = Modifier
            .width(300.dp)
            .height(40.dp)
            .clickable(
                onClick = { if (!downloadState.isDownloading) action() },
                indication = rememberRipple(
                    bounded = true,
                    radius = if (!downloadState.isDownloading) 60.dp else 0.dp,
                    color = Color.Blue.copy(alpha = 0.3f)
                ),
                interactionSource = remember { MutableInteractionSource() }
            )
            .background(
                brush = Brush.horizontalGradient(
                    colorStops = downloadState.getGradients()
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(9.dp),
        text = if (downloadState.isCompleted)
            "Downloaded"
        else if (!downloadState.isDownloading)
            "Download"
        else "Downloading ${downloadState.progress?.times(100)?.toInt() ?: 0}%",
        color = Color.White,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )
}

fun DownloadState.getGradients(): Array<Pair<Float, Color>> {
    if (isCompleted) return arrayOf(
        (progress ?: 1f) to LeafGreen,
        (progress ?: 1f) to LeafGreen
    )
    return arrayOf(
        (progress ?: 1f) to YouTubeRed,
        (progress ?: 1f) to Color.Blue.copy(alpha = 0.3f)
    )
}
