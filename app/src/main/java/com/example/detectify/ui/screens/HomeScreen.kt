package com.example.detectify.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.detectify.data.FirebaseRepo

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

    val email = user?.email ?: "User"
    val shortEmail = email.take(6)

    var photoUrl by remember { mutableStateOf(user?.photoUrl?.toString()) }

    // Time greeting
    val hour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
    val greeting = when (hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..20 -> "Good Evening"
        else -> "Good Night"
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            launchUpload(ctx, it) { url ->
                photoUrl = url
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detectify",
                        color = Color(0xFF1565C0),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {
                        FirebaseRepo.signOut()
                        onLogout()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(20.dp))

            // BIGGER profile picture
            Box(
                modifier = Modifier
                    .size(200.dp)   // increased size
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFE0E0E0))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (photoUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(photoUrl),
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("Add Photo", fontSize = 18.sp)
                }
            }

            Spacer(Modifier.height(25.dp))

            // Dynamic greeting
            Text(
                text = "$greeting, $shortEmail 👋",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            // Friendly message
            Text(
                text = "Hope you're doing well!",
                fontSize = 18.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium //Text view
            )
        }
    }
}

private fun launchUpload(ctx: android.content.Context, uri: Uri, onDone: (String?) -> Unit) {
    kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
        val result = FirebaseRepo.uploadProfileImage(uri)
        kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
            if (result.isSuccess) {
                Toast.makeText(ctx, "Uploaded!", Toast.LENGTH_SHORT).show()
                onDone(result.getOrNull())
            } else {
                Toast.makeText(ctx, "Upload failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
