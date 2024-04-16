package com.example.finalpaper

import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.Composable
import androidx.navigation.NavController


@Composable
fun MagnifierScreen(
    controller: LifecycleCameraController,
    navController: NavController
) {
    CameraPreview(controller = controller)
}