package com.example.detectify.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.detectify.ui.theme.AuroraPink
import com.example.detectify.ui.theme.DeepSpace
import com.example.detectify.ui.theme.ElectricCyan
import com.example.detectify.ui.theme.ElectricViolet
import com.example.detectify.ui.theme.Midnight

@Composable
fun DetectifyGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infinite = rememberInfiniteTransition(label = "bg-shift")
    val offset by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(
                durationMillis = 12000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bg-offset"
    )
    val orbitShift by infinite.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(
                durationMillis = 18000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbital-shift"
    )

    val gradient = Brush.linearGradient(
        colors = listOf(Midnight, DeepSpace, ElectricViolet),
        start = Offset(0f, offset),
        end = Offset(1000f, offset + 1000f)
    )

    Box(
        modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .blur(260.dp)
        ) {
            val width = size.width
            val height = size.height
            val orbRadius = width * 0.35f
            val cyanCenter = Offset(width * orbitShift, height * 0.15f)
            val violetCenter = Offset(width * (1 - orbitShift), height * 0.65f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ElectricCyan.copy(alpha = 0.45f), Color.Transparent),
                    center = cyanCenter,
                    radius = orbRadius
                ),
                center = cyanCenter,
                radius = orbRadius
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AuroraPink.copy(alpha = 0.45f), Color.Transparent),
                    center = violetCenter,
                    radius = orbRadius * 1.1f
                ),
                center = violetCenter,
                radius = orbRadius * 1.1f
            )
        }

        Canvas(
            modifier = Modifier
                .matchParentSize()
                .blur(140.dp)
        ) {
            drawRect(
                color = Color.White.copy(alpha = 0.04f),
                size = size
            )

            val shimmerBrush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.08f), Color.Transparent),
                start = Offset(0f, size.height * orbitShift),
                end = Offset(size.width, size.height * (orbitShift + 0.3f))
            )
            drawRect(
                brush = shimmerBrush,
                size = size,
                blendMode = BlendMode.Plus
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(AuroraPink.copy(alpha = 0.3f), Color.Transparent),
                        center = Offset(200f, 200f),
                        radius = 900f
                    )
                )
        )
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

