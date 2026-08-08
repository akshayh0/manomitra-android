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

    // Onboarding screens
    object OnboardingWelcome : Screen("onboarding_welcome")
    object OnboardingInsights : Screen("onboarding_insights")
    object OnboardingAI : Screen("onboarding_ai")

    // Primary application dashboard and feature areas
    object Home : Screen("home")
    object Chat : Screen("chat")
    object Mood : Screen("mood")
    object Journal : Screen("journal")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object VoiceCompanion : Screen("voice_companion")
    object EditProfile : Screen("edit_profile")
    object DailyInsightDetail : Screen("daily_insight_detail")
}
