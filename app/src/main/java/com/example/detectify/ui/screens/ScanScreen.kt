package com.example.detectify.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.example.detectify.BuildConfig
import com.example.detectify.ui.components.GlassCard
import com.example.detectify.ui.components.NeonButton
import com.example.detectify.util.TtsHelper
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ScanScreen(onBack: () -> Unit) {

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val tts = remember { TtsHelper(ctx) }
    var resultText by remember { mutableStateOf("Point the camera at an object") }
    var isProcessing by remember { mutableStateOf(false) }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(ctx) }
    val previewView = remember { PreviewView(ctx) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // ----------------------------
    // CAMERA BINDING
    // ----------------------------
    LaunchedEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            ctx as LifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageCapture
        )
    }

    // ----------------------------
    // GEMINI MODEL
    // ----------------------------
    val model = remember {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Object Scan") },
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // CAMERA PREVIEW
            GlassCard {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                ) {
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // RESULT BOX
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Detected Object", style = MaterialTheme.typography.titleMedium)

                    AnimatedContent(
                        targetState = resultText,
                        transitionSpec = { fadeIn() togetherWith fadeOut() }
                    ) { txt ->
                        Text(
                            text = txt,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // SCAN BUTTON
            NeonButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (isProcessing) return@NeonButton
                    isProcessing = true

                    val file = File(ctx.cacheDir, "scan_image.jpg")
                    val output = ImageCapture.OutputFileOptions.Builder(file).build()

                    imageCapture.takePicture(
                        output,
                        cameraExecutor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onError(exc: ImageCaptureException) {
                                resultText = "Capture failed!"
                                isProcessing = false
                            }

                            override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                                val bitmap = BitmapFactory.decodeFile(file.absolutePath)

                                scope.launch(Dispatchers.IO) {
                                    resultText = "Analyzing…"

                                    val finalText = sendToGemini(bitmap)
                                    resultText = finalText
                                    tts.speak(finalText)

                                    isProcessing = false
                                }
                            }
                        }
                    )
                }
            ) {
                Text(if (isProcessing) "Scanning…" else "Scan")
            }
        }
    }
}

/* ----------------------------------------------------
   FIXED: Stable Gemini Request With Retry
---------------------------------------------------- */
private suspend fun sendToGemini(bitmap: Bitmap): String {
    val model = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    repeat(3) { attempt ->
        try {
            val response = model.generateContent(
                content {
                    image(bitmap)
                    text(
                        """Identify the main object in this image. 
                           Reply ONLY with the object name."""
                    )
                }
            )
            return response.text ?: "No result"
        } catch (e: Exception) {
            if (attempt == 2) return "Try again later: ${e.message}"
            delay(400) // small retry delay
        }
    }
    return "Unknown error"
}
