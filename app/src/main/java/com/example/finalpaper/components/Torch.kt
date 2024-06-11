package com.example.finalpaper.components

import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import com.example.finalpaper.audioUtilities.TextToSpeechController


@Composable
fun Torch(controller: LifecycleCameraController, ttsController: TextToSpeechController) {
    var isFlashlightOn by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            isFlashlightOn = !isFlashlightOn
            controller.enableTorch(isFlashlightOn)
            ttsController.speakInterruptingly(
                "Flashlight ${
                    if (isFlashlightOn) {
                        "on"
                    } else {
                        "off"
                    }
                }"
            )
        },
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .height(80.dp)
            .width(80.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (!isFlashlightOn) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
            contentColor = if (!isFlashlightOn) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_flash_on),
            contentDescription = "Flashlight",
        )
    }
}