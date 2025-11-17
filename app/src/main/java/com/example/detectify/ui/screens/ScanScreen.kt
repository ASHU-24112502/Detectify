package com.example.detectify.ui.screens

import android.graphics.BitmapFactory
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.detectify.BuildConfig
import com.example.detectify.util.TtsHelper
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(onBack: () -> Unit) {

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val tts = remember { TtsHelper(ctx) }
    var resultText by remember { mutableStateOf("Point the camera at an object") }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(ctx) }
    val previewView = remember { PreviewView(ctx) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Setup Camera
    LaunchedEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            ctx as androidx.lifecycle.LifecycleOwner,
            androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageCapture
        )
    }

    // Setup Gemini Model
    val model = remember {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Object") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Camera Preview
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Scan Button
            Button(
                onClick = {
                    val file = File(ctx.cacheDir, "scan_image.jpg")
                    val output = ImageCapture.OutputFileOptions.Builder(file).build()

                    imageCapture.takePicture(
                        output,
                        cameraExecutor,
                        object : ImageCapture.OnImageSavedCallback {

                            override fun onError(exc: ImageCaptureException) {
                                resultText = "Capture failed!"
                            }

                            override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                                val bitmap = BitmapFactory.decodeFile(file.absolutePath)

                                scope.launch(Dispatchers.IO) {
                                    try {
                                        val response = model.generateContent(
                                            Content.Builder()
                                                .image(bitmap)
                                                .text("Identify the object. Respond with only the main object name.")
                                                .build()
                                        )

                                        val text = response.text ?: "Couldn't identify"
                                        resultText = text
                                        tts.speak(text)

                                    } catch (e: Exception) {
                                        resultText = "Scan failed: ${e.message}"
                                    }
                                }
                            }
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Scan")
            }

            Spacer(Modifier.height(12.dp))

            Text(resultText, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
