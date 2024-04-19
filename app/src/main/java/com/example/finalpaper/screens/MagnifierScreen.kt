package com.example.finalpaper.screens

import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finalpaper.components.BottomBar
import com.example.finalpaper.components.CameraPreview


@Composable
fun MagnifierScreen(
    controller: LifecycleCameraController,
    navController: NavController
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(controller = controller, Modifier.fillMaxSize())
        Column (modifier = Modifier.align(Alignment.BottomCenter)
            .padding(40.dp)){
            BottomBar(controller)
        }

    }
}