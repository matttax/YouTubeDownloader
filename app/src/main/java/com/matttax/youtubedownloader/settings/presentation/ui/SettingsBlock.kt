package com.matttax.youtubedownloader.settings.presentation.ui

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsBlock(
    text: String,
    settings: @Composable () -> Unit
) {
    Divider(modifier = Modifier.padding(vertical = 15.dp))
    Text(
        modifier = Modifier.offset(x = 15.dp).padding(bottom = 7.dp),
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    settings()
}
