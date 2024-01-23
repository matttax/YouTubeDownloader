package com.matttax.youtubedownloader.library.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Title(text: String) {
    Text(
        modifier = Modifier.padding(
            horizontal = 15.dp,
            vertical = 5.dp
        ),
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
}
