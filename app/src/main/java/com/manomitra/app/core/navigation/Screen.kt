package com.manomitra.app.core.navigation

/*
 * Navigation Route Definitions
 *
 * Defines the screen routing paths for the application using a sealed class.
 * This ensures that navigation routes are type-safe and centrally managed.
 */
sealed class Screen(val route: String) {
    // Splash screen (entry point)
    object Splash : Screen("splash")

    // Authentication screens
    object Login : Screen("login")
    object Register : Screen("register")

    // Primary application dashboard and feature areas
    object Home : Screen("home")
    object Chat : Screen("chat")
    object Mood : Screen("mood")
    object Journal : Screen("journal")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}
