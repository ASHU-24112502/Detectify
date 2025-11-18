package com.example.detectify.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.detectify.ui.components.DetectifyGradientBackground
import com.example.detectify.ui.components.GlassCard
import com.example.detectify.ui.components.NeonButton
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WelcomeScreen(onLogin: () -> Unit, onSignup: () -> Unit) {
    val scroll = rememberScrollState()
    var pulse by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            pulse = (pulse + 1) % fancyFacts.size
        }
    }

    DetectifyGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            HeroHeader(pulse = pulse)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HeroStatCard(
                    title = "0.3s",
                    subtitle = "Avg. AI response",
                    modifier = Modifier.weight(1f)
                )
                HeroStatCard(
                    title = "24/7",
                    subtitle = "Voice guidance uptime",
                    modifier = Modifier.weight(1f)
                )
            }

            ExperienceCard()

            GlassCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Assistive superpowers",
                        style = MaterialTheme.typography.titleLarge
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("Camera AI", "Assistive Tech", "Secure Cloud", "Voice-ready", "Haptic cues").forEach {
                            FeaturePill(text = it)
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NeonButton(modifier = Modifier.fillMaxWidth(), onClick = onLogin) {
                    Text("Sign in to continue", style = MaterialTheme.typography.titleMedium)
                }
                OutlinedButton(
                    onClick = onSignup,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create an account")
                }
            }
        }
    }
}

@Composable
private fun HeroHeader(pulse: Int) {
    GlassCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "AI Visual Assistant",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
            )
            Text(
                text = "Detectify",
                style = MaterialTheme.typography.displayLarge,
                fontSize = 50.sp
            )
            Text(
                text = fancyFacts[pulse],
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.alpha(0.9f)
            )
        }
    }
}

@Composable
private fun HeroStatCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.alpha(0.85f))
        }
    }
}

@Composable
private fun ExperienceCard() {
    GlassCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Built for confident navigation",
                style = MaterialTheme.typography.titleLarge
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "Fast object understanding backed by Gemini vision models",
                    "Instant spoken descriptions with human-friendly language",
                    "Privacy-first cloud sync aligned with UN SDG #9 goals"
                ).forEach { point ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text("✦", modifier = Modifier.padding(end = 8.dp))
                        Text(point, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturePill(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                )
            )
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

private val fancyFacts = listOf(
    "Empowering people of all abilities to navigate their surroundings with confidence.",
    "Designed with haptics, high contrast and voice to keep accessibility first.",
    "Your privacy stays protected — every scan is encrypted end to end."
)
