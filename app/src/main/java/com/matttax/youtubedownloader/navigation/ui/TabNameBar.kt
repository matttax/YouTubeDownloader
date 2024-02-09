package com.matttax.youtubedownloader.navigation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TabNameBar(name: String) {
    AnimatedContent(
        targetState = name,
        transitionSpec = {
            EnterTransition.None togetherWith ExitTransition.None
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .background(
                        color = YouTubeRed,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 3.dp)
                    .animateEnterExit(
                        enter = scaleIn(),
                        exit = scaleOut()
                    ),
                text = name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
