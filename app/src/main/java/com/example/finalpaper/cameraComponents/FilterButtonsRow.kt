package com.example.finalpaper.cameraComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.example.finalpaper.R
import com.example.finalpaper.audioUtilities.TextToSpeechController

@Composable
fun FilterButtonsRow(
    sobelImageBitmap: ImageBitmap?,
    sharpenImageBitmap: ImageBitmap?,
    unsharpMaskImageBitmap: ImageBitmap?,
    isSobelApplied: Boolean,
    isSharpenApplied: Boolean,
    isUnsharpMaskApplied: Boolean,
    ttsController: TextToSpeechController,
    onApplyFilter: (ImageBitmap?, Boolean, Boolean, Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
    ) {
        sobelImageBitmap?.let {
            FilterButton(
                isApplied = isSobelApplied,
                ttsMessage = "Sobel filter",
                ttsController = ttsController,
                onApplyFilter = { isApplied ->
                    onApplyFilter(if (!isApplied) null else it, isApplied, false, false)
                },
                filterIcon = R.drawable.baseline_filter_1_24
            )
        }
        sharpenImageBitmap?.let {
            FilterButton(
                isApplied = isSharpenApplied,
                ttsMessage = "Sharpen filter",
                ttsController = ttsController,
                onApplyFilter = { isApplied ->
                    onApplyFilter(if (!isApplied) null else it, false, isApplied, false)
                },
                filterIcon = R.drawable.baseline_filter_2_24
            )
        }
        unsharpMaskImageBitmap?.let {
            FilterButton(
                isApplied = isUnsharpMaskApplied,
                ttsMessage = "Unsharp mask filter",
                ttsController = ttsController,
                onApplyFilter = { isApplied ->
                    onApplyFilter(if (!isApplied) null else it, false, false, isApplied)
                },
                filterIcon = R.drawable.baseline_filter_3_24
            )
        }
    }
}