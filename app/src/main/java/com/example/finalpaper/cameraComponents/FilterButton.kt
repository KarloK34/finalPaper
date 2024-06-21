package com.example.finalpaper.cameraComponents

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.finalpaper.audioUtilities.TextToSpeechController

@Composable
fun FilterButton(
    isApplied: Boolean,
    ttsMessage: String,
    ttsController: TextToSpeechController,
    onApplyFilter: (Boolean) -> Unit,
    @DrawableRes filterIcon: Int
) {
    IconButton(
        onClick = {
            ttsController.speakInterruptingly(if (isApplied) "Remove $ttsMessage" else "Apply $ttsMessage")
            onApplyFilter(!isApplied)
        },
        modifier = Modifier
            .padding(start = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .height(65.dp)
            .width(65.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (!isApplied) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
            contentColor = if (!isApplied) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Icon(
            painter = painterResource(id = filterIcon),
            contentDescription = "Apply or Undo $ttsMessage"
        )
    }
}