package com.example.detectify.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.detectify.ui.screens.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "home" else "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(
                onLogin = { navController.navigate("auth") },
                onSignup = { navController.navigate("auth") }
            )
        }

        composable("auth") {
            AuthScreen(
                onSuccess = {
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onScan = { navController.navigate("scan") },
                onHelp = { navController.navigate("help") },
                onSettings = { navController.navigate("settings") },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("welcome") {
                        popUpTo(0)
                    }
                }
            )
        }

        composable("scan") {
            ScanScreen(onBack = { navController.popBackStack() })
        }

        composable("settings") {
            SettingScreen(onBack = { navController.popBackStack() })
        }

        composable("help") {
            HelpScreen(onBack = { navController.popBackStack() })
        }
    }
}
