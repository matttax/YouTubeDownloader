package com.matttax.youtubedownloader.navigation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

object NavigationAnimations {
    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? = {
        fadeIn(
            animationSpec = tween(500)
        )
    }

    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? = {
        fadeOut(
            animationSpec = tween(500)
        )
    }
}