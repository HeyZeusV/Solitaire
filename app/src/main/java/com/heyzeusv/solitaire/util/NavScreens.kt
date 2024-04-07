package com.heyzeusv.solitaire.util

/**
 *  Possible screens to navigate to using [route].
 */
sealed class NavScreens(val route: String) {
    data object Splash : NavScreens("splash_screen")
    data object Game : NavScreens("game_screen")
}