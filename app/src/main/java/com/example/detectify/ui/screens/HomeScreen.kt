package com.example.detectify.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.detectify.data.FirebaseRepo
import com.example.detectify.ui.components.DetectifyGradientBackground
import com.example.detectify.ui.components.GlassCard
import com.example.detectify.ui.components.NeonButton
import com.example.detectify.ui.theme.AuroraPink
import com.example.detectify.ui.theme.ElectricCyan
import com.example.detectify.ui.theme.ElectricViolet
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.vector.ImageVector

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
    val shortEmail = email.substringBefore("@")

    var photoUrl by remember { mutableStateOf(user?.photoUrl?.toString()) }

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

    DetectifyGradientBackground {
        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Detectify", fontWeight = FontWeight.Bold) },
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
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                GlassCard {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (photoUrl != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(photoUrl),
                                    contentDescription = "Profile",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text("Add Photo", fontSize = 16.sp)
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$greeting, $shortEmail 👋",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        NeonButton(onClick = onScan, modifier = Modifier.fillMaxWidth()) {
                            Text("Start a new scan")
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatusTag(text = "Voice ready")
                            StatusTag(text = "Camera steady")
                            StatusTag(text = "Cloud secure")
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AssistMetricCard(
                        title = "Scan streak",
                        value = "3 days",
                        footer = "Keep the flow going",
                        modifier = Modifier.weight(1f)
                    )
                    AssistMetricCard(
                        title = "Voice clarity",
                        value = "98%",
                        footer = "Headphones recommended",
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "Quick actions",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    QuickActionCard(
                        title = "Scan",
                        subtitle = "Camera + Voice",
                        icon = Icons.Default.Videocam,
                        colors = listOf(ElectricCyan, ElectricViolet),
                        onClick = onScan,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Help",
                        subtitle = "Contact support",
                        icon = Icons.Default.HelpCenter,
                        colors = listOf(AuroraPink, ElectricViolet),
                        onClick = onHelp,
                        modifier = Modifier.weight(1f)
                    )
                }

                QuickActionCard(
                    title = "Settings",
                    subtitle = "Language & preferences",
                    icon = Icons.Default.Settings,
                    colors = listOf(ElectricCyan, AuroraPink),
                    onClick = onSettings,
                    modifier = Modifier.fillMaxWidth()
                )

                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Assistive insights", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "• Hold steady for sharper detections\n" +
                                "• Double tap scan button for quick repeats\n" +
                                "• Use headphones to keep feedback private",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusTag(text: String) {
    Surface(
        shape = RoundedCornerShape(40),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun AssistMetricCard(
    title: String,
    value: String,
    footer: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(footer, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    colors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(28.dp)
    Surface(
        modifier = modifier
            .height(150.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        color = Color.Transparent,
        shape = shape
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(colors),
                    shape = shape
                )
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White)
                }
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                }
            }
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
