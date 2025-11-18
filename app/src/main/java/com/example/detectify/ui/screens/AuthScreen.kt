package com.example.detectify.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.detectify.data.FirebaseRepo
import com.example.detectify.ui.components.DetectifyGradientBackground
import com.example.detectify.ui.components.GlassCard
import com.example.detectify.ui.components.GlowingTextField
import com.example.detectify.ui.components.NeonButton
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(onSuccess: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val ctx = LocalContext.current

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var keepSignedIn by remember { mutableStateOf(true) }
    val passwordStrength = remember(password) { calculatePasswordStrength(password) }

    DetectifyGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Welcome back", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Sign in or create an account to continue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }

            Spacer(Modifier.height(24.dp))

            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AuthAssistChip("Voice ready")
                        AuthAssistChip("Secure sync")
                        AuthAssistChip("Dark mode")
                    }

                    GlowingTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null
                            )
                        }
                    )

                    GlowingTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        placeholder = "Minimum 6 characters",
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (showPassword) "Hide password" else "Show password"
                                )
                            }
                        }
                    )

                    PasswordStrengthBar(strength = passwordStrength)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Keep me signed in", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = keepSignedIn, onCheckedChange = { keepSignedIn = it })
                    }

                    NeonButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(ctx, "Enter email & password", Toast.LENGTH_SHORT).show()
                                return@NeonButton
                            }
                            loading = true
                            coroutineScope.launch {
                                val result = FirebaseRepo.signInOrCreate(email.trim(), password)
                                loading = false
                                if (result.isSuccess) {
                                    Toast.makeText(ctx, "Signed in", Toast.LENGTH_SHORT).show()
                                    onSuccess()
                                } else {
                                    Toast.makeText(
                                        ctx,
                                        "Auth failed: ${result.exceptionOrNull()?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    ) {
                        Text(if (loading) "Please wait..." else "Continue")
                    }

                    Text(
                        text = "By continuing, you agree to the Detectify terms and privacy policy.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthAssistChip(text: String) {
    Surface(
        shape = RoundedCornerShape(40),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun PasswordStrengthBar(strength: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Password strength", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(
                text = when {
                    strength > 0.75f -> "Strong"
                    strength > 0.45f -> "Okay"
                    else -> "Weak"
                },
                style = MaterialTheme.typography.labelMedium
            )
        }
        LinearProgressIndicator(
            progress = { strength },
            modifier = Modifier.fillMaxWidth(),
            trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
        )
    }
}

private fun calculatePasswordStrength(password: String): Float {
    if (password.isBlank()) return 0f
    var strength = 0f
    if (password.length >= 6) strength += 0.3f
    if (password.any { it.isUpperCase() }) strength += 0.2f
    if (password.any { it.isDigit() }) strength += 0.3f
    if (password.any { !it.isLetterOrDigit() }) strength += 0.2f
    return strength.coerceIn(0f, 1f)
}
