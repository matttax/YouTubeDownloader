package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed
import com.matttax.youtubedownloader.youtube.presentation.ui.utils.Options
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> Spinner(
    modifier: Modifier,
    name: String,
    options: Options<T>,
    selectedOption: Flow<String>,
    optionSize: Float = 0.5f
) {
    var expanded by remember { mutableStateOf(false) }
    val selected by selectedOption.collectAsState(initial = "")

    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.onBackground)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6),
            colors = if (expanded)
                ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onBackground)
            else ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onSurface),
            onClick = { expanded = true }
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = name,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = selected,
                    fontSize = 9.sp,
                    color = YouTubeRed.copy(
                        alpha = if (isSystemInDarkTheme()) 1f else 0.5f
                    )
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.onBackground)
                .fillMaxWidth(optionSize)
        ) {
            for (option in options.argumentMap) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(start = 3.dp, end = 3.dp),
                    shape = RoundedCornerShape(20),
                    colors = if (option.key == selected)
                        ButtonDefaults.buttonColors(YouTubeRed.copy(alpha = 0.3f))
                    else ButtonDefaults.textButtonColors(),
                    onClick = {
                        options.action(option.value)
                        expanded = false
                    }
                ) {
                    Text(
                        text = option.key,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}