package com.example.finalpaper.components

import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.finalpaper.R


@Composable
fun Torch(controller: LifecycleCameraController) {
    var isFlashlightOn by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            isFlashlightOn = !isFlashlightOn
            controller.enableTorch(isFlashlightOn)
        },
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = if (isFlashlightOn) Color.Yellow else Color.LightGray
            )
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_flash_on),
            contentDescription = "Flashlight",
            tint = if (isFlashlightOn) Color.Black else Color.White
        )
    }
}