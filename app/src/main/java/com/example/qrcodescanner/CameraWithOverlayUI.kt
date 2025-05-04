package com.example.qrcodescanner

import android.content.Intent
import android.util.Patterns
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

@Composable
fun CameraWithOverlayUI(onScanned: (String) -> Unit) {
    val context = LocalContext.current
    var isScanned by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!isScanned) {
            cameraPreview(onQRCodeScanned = { scannedCode ->
                isScanned = true
                onScanned(scannedCode)

                // ✅ Automatically open scanned URL if it's valid
                if (Patterns.WEB_URL.matcher(scannedCode).matches()) {
                    val intent = Intent(Intent.ACTION_VIEW, scannedCode.toUri())
                    context.startActivity(intent)
                }
            })
        }

        // Optional overlay while scanning
        QRCodeOverlay(text = "Scanning...")
    }
}


@Composable
fun cameraPreview(onQRCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(ctx), QRCodeAnalyzer { qr ->
                            onQRCodeScanned(qr)
                        })
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageAnalyzer
                )
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun QRCodeOverlay(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(8.dp)
        )
    }
}


