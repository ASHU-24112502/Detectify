package com.example.detectify.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.detectify.data.FirebaseRepo
import com.example.detectify.ui.components.DetectifyGradientBackground
import com.example.detectify.ui.components.GlassCard
import com.example.detectify.ui.components.GlowingTextField
import com.example.detectify.ui.components.NeonButton
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onSuccess: () -> Unit, onSignup: () -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    var err by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    DetectifyGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Log back in",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Access your assistive scans and continue exploring with Detectify.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LoginAssistChip("Voice ready")
                        LoginAssistChip("Cloud sync")
                        LoginAssistChip("Secure login")
                    }

                    GlowingTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (err.isNotEmpty()) err = ""
                        },
                        label = "Email address",
                        placeholder = "you@example.com",
                        leadingIcon = {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Default.AlternateEmail,
                                contentDescription = null
                            )
                        }
                    )

                    GlowingTextField(
                        value = pass,
                        onValueChange = {
                            pass = it
                            if (err.isNotEmpty()) err = ""
                        },
                        label = "Password",
                        placeholder = "Enter password",
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconToggle(show = showPassword) { showPassword = !showPassword }
                        },
                        leadingIcon = {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null
                            )
                        }
                    )

                    AnimatedVisibility(
                        visible = err.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    NeonButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            focusManager.clearFocus()
                            loading = true
                            scope.launch {
                                val res = FirebaseRepo.signInOrCreate(email.trim(), pass)
                                loading = false
                                if (res.isSuccess) onSuccess()
                                else err = res.exceptionOrNull()?.message ?: "Something went wrong"
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (loading) "Authenticating…" else "Login",
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (loading) {
                                Spacer(Modifier.width(12.dp))
                                CircularProgressIndicator(
                                    modifier = Modifier.height(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    TextButton(onClick = onSignup, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Need an account? Sign up", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Pro tip", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Use headphones during scans to keep voice feedback private when in public spaces.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun IconToggle(show: Boolean, onToggle: () -> Unit) {
    androidx.compose.material3.IconButton(onClick = onToggle) {
        androidx.compose.material3.Icon(
            imageVector = if (show) Icons.Default.Visibility else Icons.Default.VisibilityOff,
            contentDescription = if (show) "Hide password" else "Show password"
        )
    }
}

@Composable
private fun LoginAssistChip(text: String) {
    Surface(
        shape = RoundedCornerShape(40),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
