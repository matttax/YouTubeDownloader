package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
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
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6),
            colors = if (expanded)
                ButtonDefaults.buttonColors(Color.White)
            else ButtonDefaults.buttonColors(Color.White.copy(alpha = 0.4f)),
            onClick = { expanded = true }
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = name,
                    color = Color.Black
                )
                Text(
                    text = selected,
                    fontSize = 9.sp,
                    color = YouTubeRed.copy(alpha = 0.5f)
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(optionSize)
        ) {
            for (option in options.argumentMap) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(start = 3.dp, end = 3.dp),
                    shape = RoundedCornerShape(20),
                    colors = if (option.key == selected)
                        ButtonDefaults.buttonColors(Color.Red.copy(alpha = 0.3f, red = 0.8f))
                    else ButtonDefaults.buttonColors(Color.White.copy(alpha = 0.6f)),
                    onClick = {
                        options.action(option.value)
                        expanded = false
                    }
                ) {
                    Text(
                        text = option.key,
                        color = Color.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}