package com.example.detectify.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.detectify.data.FirebaseRepo
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onSuccess: () -> Unit, onSignup: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var err by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Password") })

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                loading = true
                scope.launch {
                    val res = FirebaseRepo.signInOrCreate(email, pass)
                    loading = false
                    if (res.isSuccess) onSuccess()
                    else err = res.exceptionOrNull()?.message ?: "Failed"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (loading) "Logging in…" else "Login") }

        Spacer(Modifier.height(10.dp))

        TextButton(onClick = onSignup, modifier = Modifier.align(Alignment.End)) {
            Text("Create Account")
        }

        if (err.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text(err, color = androidx.compose.ui.graphics.Color.Red)
        }
    }
}
