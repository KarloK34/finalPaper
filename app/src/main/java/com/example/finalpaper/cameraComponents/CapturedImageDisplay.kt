package com.example.finalpaper.cameraComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.finalpaper.R
import com.example.finalpaper.audioUtilities.TextToSpeechController

@Composable
fun CapturedImageDisplay(
    isFilterApplied: Boolean,
    filteredImageBitmap: ImageBitmap?,
    capturedImageBitmap: ImageBitmap?,
    showFilterButtons: Boolean,
    sobelImageBitmap: ImageBitmap?,
    sharpenImageBitmap: ImageBitmap?,
    unsharpMaskImageBitmap: ImageBitmap?,
    isSobelApplied: Boolean,
    isSharpenApplied: Boolean,
    isUnsharpMaskApplied: Boolean,
    ttsController: TextToSpeechController,
    onApplyFilter: (ImageBitmap?, Boolean, Boolean, Boolean) -> Unit,
    onSaveImage: () -> Unit,
    onReset: () -> Unit,
    onToggleFilterButtons: () -> Unit
) {
    val imageToShow = if (isFilterApplied) filteredImageBitmap else capturedImageBitmap
    var contrast by remember { mutableFloatStateOf(1f) }
    var brightness by remember { mutableFloatStateOf(0f) }

    val colorMatrix = remember(contrast, brightness) {
        floatArrayOf(
            contrast, 0f, 0f, 0f, brightness,
            0f, contrast, 0f, 0f, brightness,
            0f, 0f, contrast, 0f, brightness,
            0f, 0f, 0f, 1f, 0f
        )
    }
    imageToShow?.let {
        Image(
            bitmap = it,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.colorMatrix(ColorMatrix(colorMatrix)),
        )
    }

    Column(
        modifier = Modifier
            .padding(40.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom
    ) {
        if (showFilterButtons) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_contrast_24),
                    contentDescription = "Contrast icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)
                )
                Slider(
                    value = contrast,
                    onValueChange = { newContrast ->
                        contrast = newContrast
                    },
                    valueRange = 0f..10f,
                    steps = 50,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_brightness_7_24),
                    contentDescription = "Contrast icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)
                )
                Slider(
                    value = brightness,
                    onValueChange = { newBrightness ->
                        brightness = newBrightness
                    },
                    valueRange = -255f..255f,
                    steps = 50,
                )
            }

            FilterButtonsRow(
                sobelImageBitmap = sobelImageBitmap,
                sharpenImageBitmap = sharpenImageBitmap,
                unsharpMaskImageBitmap = unsharpMaskImageBitmap,
                isSobelApplied = isSobelApplied,
                isSharpenApplied = isSharpenApplied,
                isUnsharpMaskApplied = isUnsharpMaskApplied,
                ttsController = ttsController,
                onApplyFilter = onApplyFilter
            )
        }
        CapturedImageButtonsRow(
            onSaveImage = onSaveImage,
            onReset = onReset,
            onToggleFilterButtons = onToggleFilterButtons,
            showFilterButtons = showFilterButtons,
        )
    }
}
