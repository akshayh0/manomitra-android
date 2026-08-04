        package com.manomitra.app.core.navigation

        import androidx.compose.runtime.Composable
        import androidx.compose.ui.Modifier
        import androidx.navigation.NavHostController
        import androidx.navigation.compose.NavHost
        import androidx.navigation.compose.composable
        import androidx.navigation.compose.rememberNavController
        import com.manomitra.app.feature.splash.SplashScreen
        import com.manomitra.app.feature.auth.login.LoginScreen
        import com.manomitra.app.feature.auth.register.RegisterScreen
        import com.manomitra.app.feature.home.HomeScreen
        import com.manomitra.app.feature.chat.ChatScreen
        import com.manomitra.app.feature.voice.VoiceCompanionScreen
        import com.manomitra.app.feature.onboarding.OnboardingWelcomeScreen
        import com.manomitra.app.feature.onboarding.OnboardingInsightsScreen
        import com.manomitra.app.feature.onboarding.OnboardingAIScreen
        import com.manomitra.app.feature.journal.JournalScreen
        import com.manomitra.app.feature.mood.MoodScreen
        import com.manomitra.app.feature.profile.ProfileScreen
        import com.manomitra.app.feature.settings.SettingsScreen
        import androidx.compose.foundation.clickable
        import androidx.compose.foundation.layout.fillMaxSize
        import androidx.compose.foundation.layout.Box
        import androidx.compose.material3.Text

        /*
        * AppNavHost
        *
        * Responsibility: Solely responsible for defining the application's navigation graph
        * and routing logic using Navigation Compose. It exposes event callbacks to individual
        * screens to maintain a clean architecture, decoupling view presentations from navigation handlers.
        */
        @Composable
        fun AppNavHost(
            modifier: Modifier = Modifier,
            navController: NavHostController = rememberNavController()
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route,
                modifier = modifier
            ) {
                // Splash Destination
                composable(route = Screen.Splash.route) {
                    SplashScreen(
                        onNavigateToLogin = {
                            navController.navigate(Screen.OnboardingWelcome.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }

                // Onboarding Welcome
                composable(route = Screen.OnboardingWelcome.route) {
                    OnboardingWelcomeScreen(
                        onContinueClick = {
                            navController.navigate(Screen.OnboardingInsights.route)
                        },
                        onSkipClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.OnboardingWelcome.route) { inclusive = true }
                            }
                        },
                        onSignInClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.OnboardingWelcome.route) { inclusive = true }
                            }
                        }
                    )
                }

                // Onboarding Insights
                composable(route = Screen.OnboardingInsights.route) {
                    OnboardingInsightsScreen(
                        onContinueClick = {
                            navController.navigate(Screen.OnboardingAI.route)
                        },
                        onSkipClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.OnboardingWelcome.route) { inclusive = true }
                            }
                        }
                    )
                }

                // Onboarding AI
                composable(route = Screen.OnboardingAI.route) {
                    OnboardingAIScreen(
                        onGetStartedClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.OnboardingWelcome.route) { inclusive = true }
                            }
                        },
                        onSkipClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.OnboardingWelcome.route) { inclusive = true }
                            }
                        }
                    )
                }

                // Login Destination
                composable(route = Screen.Login.route) {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onRegisterClick = {
                            navController.navigate(Screen.Register.route)
                        }
                    )
                }

                // Register Destination
                composable(route = Screen.Register.route) {
                    RegisterScreen(
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onRegisterSuccess = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    )
                }

                // Home Destination
                composable(route = Screen.Home.route) {
                    HomeScreen(
                        onChatClick = { navController.navigate(Screen.Chat.route) },
                        onVoiceClick = { navController.navigate(Screen.VoiceCompanion.route) },
                        onJournalClick = { navController.navigate(Screen.Journal.route) },
                        onMoodClick = { navController.navigate(Screen.Mood.route) },
                        onProfileClick = { navController.navigate(Screen.Profile.route) },
                        onSettingsClick = { navController.navigate(Screen.Settings.route) }
                    )
                }

                // Placeholder Destinations
                composable(route = Screen.Chat.route) {
                    ChatScreen(onBackClick = { navController.popBackStack() })
                }

                composable(route = Screen.VoiceCompanion.route) {
                    VoiceCompanionScreen(onBackClick = { navController.popBackStack() })
                }

                composable(route = Screen.Mood.route) {
                    MoodScreen(
                        onBackClick = { navController.popBackStack() },
                        onHomeTabClick = { navController.navigate(Screen.Home.route) },
                        onCompanionTabClick = { navController.navigate(Screen.VoiceCompanion.route) },
                        onJournalTabClick = { navController.navigate(Screen.Journal.route) },
                        onProfileTabClick = { navController.navigate(Screen.Profile.route) }
                    )
                }

                composable(route = Screen.Journal.route) {
                    JournalScreen(
                        onBackClick = { navController.popBackStack() },
                        onHomeTabClick = { navController.navigate(Screen.Home.route) },
                        onCompanionTabClick = { navController.navigate(Screen.VoiceCompanion.route) },
                        onProfileTabClick = { navController.navigate(Screen.Profile.route) }
                    )
                }

                composable(route = Screen.Profile.route) {
                    ProfileScreen(
                        onBackClick = { navController.popBackStack() },
                        onSettingsClick = { navController.navigate(Screen.Settings.route) },
                        onHomeTabClick = { navController.navigate(Screen.Home.route) },
                        onCompanionTabClick = { navController.navigate(Screen.VoiceCompanion.route) },
                        onJournalTabClick = { navController.navigate(Screen.Journal.route) },
                        onLogoutClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(route = Screen.Settings.route) {
                    SettingsScreen(onBackClick = { navController.popBackStack() })
                }
            }
        }


