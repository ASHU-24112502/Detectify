package com.example.detectify.ui.screens

import android.graphics.BitmapFactory
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.detectify.BuildConfig
import com.example.detectify.ui.components.DetectifyGradientBackground
import com.example.detectify.ui.components.GlassCard
import com.example.detectify.ui.components.NeonButton
import com.example.detectify.util.TtsHelper
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import kotlinx.coroutines.Dispatchers
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

    DetectifyGradientBackground {
        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Live Scan") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                // CAMERA VIEW
                GlassCard {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .clip(RoundedCornerShape(28.dp))
                    ) {
                        AndroidView(
                            factory = { previewView },
                            modifier = Modifier.matchParentSize()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))
                                    )
                                )
                        )
                    }
                }

                // RESULT BOX (Visible clearly)
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Detected Object", style = MaterialTheme.typography.titleMedium)
                        AnimatedContent(
                            targetState = resultText,
                            label = "scan-result",
                            transitionSpec = { fadeIn() togetherWith fadeOut() }
                        ) { text ->
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                // ONE SINGLE SCAN BUTTON
                NeonButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val file = File(ctx.cacheDir, "scan_image.jpg")
                        val output = ImageCapture.OutputFileOptions.Builder(file).build()
                        isProcessing = true

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
                                        try {
                                            val response = model.generateContent(
                                                Content.Builder()
                                                    .image(bitmap)
                                                    .text("Identify the object. Respond ONLY with object name.")
                                                    .build()
                                            )

                                            val text = response.text ?: "Couldn't identify"
                                            resultText = text
                                            tts.speak(text)
                                        } catch (e: Exception) {
                                            resultText = "Scan failed: ${e.message}"
                                        } finally {
                                            isProcessing = false
                                        }
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
}
