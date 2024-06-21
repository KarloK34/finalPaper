package com.example.finalpaper.cameraComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
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

    imageToShow?.let {
        Image(
            bitmap = it,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    Column(
        modifier = Modifier
            .padding(40.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom
    ) {
        if (showFilterButtons) {
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
            showFilterButtons = showFilterButtons
        )
    }
}
