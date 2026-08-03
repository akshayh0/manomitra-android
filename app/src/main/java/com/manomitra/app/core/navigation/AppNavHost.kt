        package com.manomitra.app.core.navigation

        import androidx.compose.runtime.Composable
        import androidx.compose.ui.Modifier
        import androidx.navigation.NavHostController
        import androidx.navigation.compose.NavHost
        import androidx.navigation.compose.composable
        import androidx.navigation.compose.rememberNavController
        import com.manomitra.app.feature.auth.login.LoginScreen
        import com.manomitra.app.feature.auth.register.RegisterScreen
        import com.manomitra.app.feature.home.HomeScreen
        import com.manomitra.app.feature.splash.SplashScreen

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
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
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
