package com.example.detectify.ui.components



import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NeonButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.border(2.dp, androidx.compose.ui.graphics.Color.Cyan, RoundedCornerShape(16.dp)).padding(2.dp),
        colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF0F172A))
    ) {
        content()
    }
}
