package com.example.finalpaper.screens

import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.finalpaper.components.CameraPreview


@Composable
fun MagnifierScreen(
    controller: LifecycleCameraController
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(controller = controller, Modifier.fillMaxSize())
    }
}