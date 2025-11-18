package com.example.detectify.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.detectify.ui.screens.*

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Auth : Screen("auth")
    object Home : Screen("home")
    object Scan : Screen("scan")
    object Settings : Screen("settings")
    object Help : Screen("help")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Welcome.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        // Welcome
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onLogin = { navController.navigate(Screen.Auth.route) },
                onSignup = { navController.navigate(Screen.Auth.route) }
            )
        }

        // Auth
        composable(Screen.Auth.route) {
            AuthScreen(onSuccess = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }

        // Home
        composable(Screen.Home.route) {
            HomeScreen(
                onScan = { navController.navigate(Screen.Scan.route) },
                onHelp = { navController.navigate(Screen.Help.route) },
                onSettings = { navController.navigate(Screen.Settings.route) },
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        // Scan
        composable(Screen.Scan.route) {
            ScanScreen(onBack = { navController.popBackStack() })
        }

        // Settings (FIXED)
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        // Help
        composable(Screen.Help.route) {
            HelpScreen(onBack = { navController.popBackStack() })
        }
    }
}
