package com.example.detectify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.detectify.ui.theme.AuroraPink
import com.example.detectify.ui.theme.ElectricCyan
import com.example.detectify.ui.theme.SolarOrange

@Composable
fun NeonButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    val gradient = Brush.horizontalGradient(
        colors = listOf(ElectricCyan, AuroraPink, SolarOrange)
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .clip(shape)
            .background(gradient)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.4f),
                shape = shape
            ),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        shape = shape,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 28.dp, vertical = 14.dp)) {
            content()
        }
    }
}
