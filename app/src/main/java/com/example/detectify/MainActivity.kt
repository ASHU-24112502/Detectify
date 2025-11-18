package com.example.detectify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.detectify.ui.NavGraph
import com.example.detectify.ui.Screen
import com.example.detectify.ui.theme.DetectifyTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Help

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DetectifyTheme {
                AppRoot()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    val navController = rememberNavController()

    // If user already logged in, start at home; otherwise welcome
    val start = if (FirebaseAuth.getInstance().currentUser != null) Screen.Home.route else Screen.Welcome.route

    // Scaffold at root so bottom bar appears across main pages
    Scaffold(
        bottomBar = {
            MainBottomBar(navController = navController)
        }
    ) { innerPadding ->
        // Pass the navController and start destination to NavGraph
        NavGraph(navController = navController, startDestination = start, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun MainBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val current = navBackStackEntry?.destination?.route

    // Bottom nav items — only four: Home, Scan, Settings, Help
    val items = listOf(
        BottomNavItem(Screen.Home.route, "Home", Icons.Default.Home),
        BottomNavItem(Screen.Scan.route, "Scan", Icons.Default.CameraAlt),
        BottomNavItem(Screen.Settings.route, "Settings", Icons.Default.Settings),
        BottomNavItem(Screen.Help.route, "Help", Icons.Default.Help)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = current == item.route,
                onClick = {
                    // navigate while avoiding building multiple copies
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                        // keep backstack minimal for main screens
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

private data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)
