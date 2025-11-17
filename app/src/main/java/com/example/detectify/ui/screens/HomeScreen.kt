package com.example.detectify.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.detectify.data.FirebaseRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.automirrored.filled.Logout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScan: () -> Unit,
    onHelp: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val ctx = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser

    var photoUrl by remember { mutableStateOf(user?.photoUrl?.toString()) }
    var displayName by remember { mutableStateOf(user?.displayName ?: "User") }

    // IMAGE PICKER
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val result = FirebaseRepo.uploadProfileImage(it)
                if (result.isSuccess) {
                    photoUrl = result.getOrNull()
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(ctx, "Uploaded successfully!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(ctx, "Upload failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detectify") },
                actions = {
                    IconButton(onClick = onHelp) {
                        Icon(Icons.Default.Info, contentDescription = "Help")
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        },

        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onScan,
                    icon = { Icon(Icons.Default.Visibility, "Scan") },
                    label = { Text("Scan") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onSettings,
                    icon = { Icon(Icons.Default.Settings, "Settings") },
                    label = { Text("Settings") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onHelp,
                    icon = { Icon(Icons.Default.Info, "Help") },
                    label = { Text("Help") }
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome, $displayName",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.DarkGray)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (photoUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(photoUrl),
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("Add Photo", color = Color.White)
                }
            }
        }
    }
}
