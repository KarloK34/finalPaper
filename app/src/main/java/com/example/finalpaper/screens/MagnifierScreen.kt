package com.example.finalpaper.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalpaper.audioUtilities.TextToSpeechController
import com.example.finalpaper.permissions.PermissionsViewModel
import com.example.finalpaper.components.CameraPreview
import com.example.finalpaper.permissions.HandleDialogs


@Composable
fun MagnifierScreen(
    controller: LifecycleCameraController,
    ttsController: TextToSpeechController
) {
    val viewModel: PermissionsViewModel = viewModel()
    val dialogQueue = viewModel.visiblePermissionDialogQueue
    val context = LocalContext.current
    var cameraPermissionGranted by remember { mutableStateOf(false) }

    val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(
            permission = Manifest.permission.CAMERA,
            isGranted = isGranted
        )
        cameraPermissionGranted = isGranted
    }

    LaunchedEffect(key1 = Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
        } else {
            cameraPermissionGranted = true
        }
    }

    HandleDialogs(
        dialogQueue = dialogQueue,
        viewModel = viewModel,
        context = context,
        ttsController = ttsController,
        permissionResultLauncher = cameraPermissionResultLauncher
    )

    if (cameraPermissionGranted) {
        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview(controller = controller, Modifier.fillMaxSize(), ttsController)
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Requesting camera permission...")
        }
    }

}