package com.example.finalpaper

import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController


@Composable
fun MagnifierScreen(
    controller: LifecycleCameraController,
    navController: NavController
) {
    CameraPreview(controller = controller, Modifier.fillMaxSize())
}