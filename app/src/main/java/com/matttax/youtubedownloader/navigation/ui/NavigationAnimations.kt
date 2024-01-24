package com.matttax.youtubedownloader.navigation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

object NavigationAnimations {
    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? by lazy {
        {
            scaleIn(
                animationSpec = tween(500)
            )
        }
    }

    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? by lazy {
        {
            scaleOut(
                animationSpec = tween(500)
            )
        }
    }
}