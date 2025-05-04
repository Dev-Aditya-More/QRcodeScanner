package com.example.qrcodescanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.runtime.Composable
import com.example.qrcodescanner.ui.theme.QRcodeScannerTheme
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            launchApp()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                100
            )
        }
    }

    // ✅ Add this function
    private fun launchApp() {
        setContent {
            QRcodeScannerTheme {
                AppContent()
            }
        }
    }

    // ✅ Handle permission result
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            launchApp()
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }


}

@Composable
fun AppContent() {
    var showScanner by remember { mutableStateOf(false) }
    var scannedText by remember { mutableStateOf("") }

    if (showScanner) {
        CameraWithOverlayUI(
            onScanned = {
                scannedText = it
                showScanner = false
            }
        )
    } else {
        HomeScreen(
            scannedResult = scannedText,
            onScanClick = { showScanner = true }
        )
    }
}

