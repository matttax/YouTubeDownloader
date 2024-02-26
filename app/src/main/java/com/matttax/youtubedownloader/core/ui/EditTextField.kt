package com.matttax.youtubedownloader.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed

@Composable
fun EditTextField(
    initialValue: String,
    hint: String,
    onChange: (newValue: String) -> Unit
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .height(50.dp),
        value = initialValue,
        onValueChange = { onChange(it) },
        placeholder = { Text(text = hint, fontSize = 12.sp) },
        singleLine = true,
        shape = RoundedCornerShape(20),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = YouTubeRed,
            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
            unfocusedContainerColor = MaterialTheme.colorScheme.onBackground,
        ),
        textStyle = TextStyle.Default.copy(
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    )
}