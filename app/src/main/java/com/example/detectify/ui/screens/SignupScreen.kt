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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.detectify.data.FirebaseRepo
import com.example.detectify.ui.components.DetectifyGradientBackground
import com.example.detectify.ui.components.GlassCard
import com.example.detectify.ui.components.GlowingTextField
import com.example.detectify.ui.components.NeonButton
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(onSuccess: () -> Unit, onLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var err by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    DetectifyGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create your account",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Unlock AI powered assistance with less than a minute of setup.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(Modifier.height(24.dp))

            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SignupPill("Secure cloud")
                        SignupPill("Dark mode")
                        SignupPill("Voice ready")
                    }
                    GlowingTextField(value = email, onValueChange = {
                        email = it
                        if (err.isNotEmpty()) err = ""
                    }, label = "Email")
                    GlowingTextField(value = pass, onValueChange = {
                        pass = it
                        if (err.isNotEmpty()) err = ""
                    }, label = "Password", placeholder = "Minimum 6 characters")

                    NeonButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            loading = true
                            scope.launch {
                                val res = FirebaseRepo.signInOrCreate(email, pass)
                                loading = false
                                if (res.isSuccess) onSuccess()
                                else err = res.exceptionOrNull()?.message ?: "Failed"
                            }
                        }
                    ) {
                        Text(if (loading) "Creating…" else "Sign Up")
                    }

                    TextButton(onClick = onLogin) {
                        Text("Already registered? Login")
                    }

                    AnimatedVisibility(
                        visible = err.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(err, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun SignupPill(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
