package com.example.finalpaper.components

import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomBar(controller: LifecycleCameraController) {
    Row(modifier = Modifier.fillMaxWidth().height(30.dp)) {
        Torch(controller)
    }
}


