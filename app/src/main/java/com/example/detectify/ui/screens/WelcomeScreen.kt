package com.example.detectify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(onLogin: () -> Unit, onSignup: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(color = androidx.compose.ui.graphics.Color.Black)) {
        Column(modifier = Modifier.align(Alignment.Center).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Detectify", fontSize = 40.sp, color = androidx.compose.ui.graphics.Color.Cyan)
            Spacer(Modifier.height(8.dp))
            Text(text = "initiated with 9th-SDG Goal", color = androidx.compose.ui.graphics.Color.LightGray)
            Spacer(Modifier.height(40.dp))
            Button(onClick = onLogin, modifier = Modifier.fillMaxWidth()) { Text("Login") }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onSignup, modifier = Modifier.fillMaxWidth()) { Text("Sign up") }
        }
    }
}
