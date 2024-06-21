package com.example.finalpaper.cameraComponents

import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import com.example.finalpaper.audioUtilities.TextToSpeechController
import com.example.finalpaper.cameraUtilities.saveImageToGallery
import com.example.finalpaper.filters.applySharpenFilter
import com.example.finalpaper.filters.applySobelFilter
import com.example.finalpaper.filters.applyUnsharpMaskFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier,
    ttsController: TextToSpeechController
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val cameraExecutor = ContextCompat.getMainExecutor(context)

    var capturedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var filteredImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var sobelImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var unsharpMaskImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var sharpenImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    var isFrozen by remember { mutableStateOf(false) }
    var isFilterApplied by remember { mutableStateOf(false) }
    var isSobelApplied by remember { mutableStateOf(false) }
    var isSharpenApplied by remember { mutableStateOf(false) }
    var isUnsharpMaskApplied by remember { mutableStateOf(false) }
    var showFilterButtons by remember { mutableStateOf(false) }

    Box {
        if (capturedImageBitmap != null && isFrozen) {
            CapturedImageDisplay(
                isFilterApplied = isFilterApplied,
                filteredImageBitmap = filteredImageBitmap,
                capturedImageBitmap = capturedImageBitmap,
                showFilterButtons = showFilterButtons,
                sobelImageBitmap = sobelImageBitmap,
                sharpenImageBitmap = sharpenImageBitmap,
                unsharpMaskImageBitmap = unsharpMaskImageBitmap,
                isSobelApplied = isSobelApplied,
                isSharpenApplied = isSharpenApplied,
                isUnsharpMaskApplied = isUnsharpMaskApplied,
                ttsController = ttsController,
                onApplyFilter = { filterBitmap, sobel, sharpen, unsharp ->
                    filteredImageBitmap = filterBitmap
                    isSobelApplied = sobel
                    isSharpenApplied = sharpen
                    isUnsharpMaskApplied = unsharp
                    isFilterApplied = sobel || sharpen || unsharp
                },
                onSaveImage = {
                    if (isFilterApplied) {
                        saveImageToGallery(filteredImageBitmap!!.asAndroidBitmap(), context)
                        ttsController.speakInterruptingly("Filtered photo saved to gallery")
                    } else {
                        saveImageToGallery(capturedImageBitmap!!.asAndroidBitmap(), context)
                        ttsController.speakInterruptingly("Photo saved to gallery")
                    }
                },
                onReset = {
                    capturedImageBitmap = null
                    filteredImageBitmap = null
                    sobelImageBitmap = null
                    sharpenImageBitmap = null
                    unsharpMaskImageBitmap = null
                    isFrozen = false
                    isFilterApplied = false
                    isSobelApplied = false
                    isSharpenApplied = false
                    isUnsharpMaskApplied = false
                    showFilterButtons = false
                },
                onToggleFilterButtons = {
                    showFilterButtons = !showFilterButtons
                }
            )
        } else {
            CameraPreviewDisplay(controller, lifecycleOwner, modifier)
        }

        if (!isFrozen) {
            CameraControls(
                controller = controller,
                ttsController = ttsController,
                cameraExecutor = cameraExecutor,
                onCaptureImage = { imageBitmap ->
                    capturedImageBitmap = imageBitmap
                    isFrozen = true

                    scope.launch(Dispatchers.Default) {
                        val sobelImage = applySobelFilter(imageBitmap)
                        val sharpenImage = applySharpenFilter(imageBitmap)
                        val unsharpMaskImage = applyUnsharpMaskFilter(imageBitmap)
                        withContext(Dispatchers.Main) {
                            sobelImageBitmap = sobelImage
                            sharpenImageBitmap = sharpenImage
                            unsharpMaskImageBitmap = unsharpMaskImage
                        }
                    }
                }
            )
        }
    }
}