        package com.manomitra.app.core.navigation

        import androidx.compose.runtime.Composable
        import androidx.compose.ui.Modifier
        import androidx.navigation.NavHostController
        import androidx.navigation.compose.NavHost
        import androidx.navigation.compose.composable
        import androidx.navigation.compose.rememberNavController
        import com.manomitra.app.feature.splash.SplashScreen
        import androidx.compose.ui.text.style.TextAlign
        import com.manomitra.app.feature.onboarding.OnboardingWelcomeScreen
        import com.manomitra.app.feature.onboarding.OnboardingInsightsScreen
        import com.manomitra.app.feature.onboarding.OnboardingAIScreen
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
                        }
                    )
                }

                // Home Destination
                composable(route = Screen.Home.route) {
                    HomeScreen()
                }
            }
        }

        @Composable
        private fun LoginScreen(
            onLoginSuccess: () -> Unit,
            onRegisterClick: () -> Unit
        ) {
            Box(
                modifier = Modifier.fillMaxSize().clickable { onLoginSuccess() },
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "Login Screen (Coming Soon)\nTap to Log In",
                    textAlign = TextAlign.Center
                )
            }
        }

        @Composable
        private fun RegisterScreen(
            onBackClick: () -> Unit
        ) {
            Box(
                modifier = Modifier.fillMaxSize().clickable { onBackClick() },
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "Register Screen (Coming Soon)\nTap to go back",
                    textAlign = TextAlign.Center
                )
            }
        }

        @Composable
        private fun HomeScreen() {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "Home Screen (Coming Soon)",
                    textAlign = TextAlign.Center
                )
            }
        }
