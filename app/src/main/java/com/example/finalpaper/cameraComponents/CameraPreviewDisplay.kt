package com.example.finalpaper.cameraComponents

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner

@Composable
fun CameraPreviewDisplay(
    controller: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
    modifier: Modifier
) {
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier
    )
}