package com.example.finalpaper.components

import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.finalpaper.R
import com.example.finalpaper.audioUtilities.TextToSpeechController
import com.example.finalpaper.cameraUtilities.convertImageProxyToBitmap
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
    var capturedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var filteredImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var sobelImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var unsharpMaskImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var sharpenImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val cameraExecutor = ContextCompat.getMainExecutor(context)
    var isFrozen by remember { mutableStateOf(false) }
    var isFilterApplied by remember { mutableStateOf(false) }
    var isSobelApplied by remember { mutableStateOf(false) }
    var isSharpenApplied by remember { mutableStateOf(false) }
    var isUnsharpMaskApplied by remember { mutableStateOf(false) }
    var showFilterButtons by remember { mutableStateOf(false) }

    Box {
        if (capturedImageBitmap != null && isFrozen) {
            (if (isFilterApplied) {
                filteredImageBitmap
            } else {
                capturedImageBitmap
            })?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(30.dp)
                    .fillMaxWidth()
            ) {
                if (showFilterButtons) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth()
                    ) {
                        if (sobelImageBitmap != null) {
                            IconButton(
                                onClick = {
                                    if (isSobelApplied) ttsController.speakInterruptingly("Remove Sobel filter")
                                    else ttsController.speakInterruptingly("Apply Sobel filter")
                                    isFilterApplied =
                                        if (isSharpenApplied || isUnsharpMaskApplied) true
                                        else !isFilterApplied
                                    isSobelApplied = !isSobelApplied
                                    isUnsharpMaskApplied = false
                                    isSharpenApplied = false
                                    filteredImageBitmap = sobelImageBitmap
                                },
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .height(65.dp)
                                    .width(65.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if(!isSobelApplied) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = if (!isSobelApplied) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_filter_1_24),
                                    contentDescription = "Apply or Undo Sobel Filter"
                                )
                            }
                        }
                        if (sharpenImageBitmap != null) {
                            IconButton(
                                onClick = {
                                    if (isSharpenApplied) ttsController.speakInterruptingly("Remove Sharpen filter")
                                    else ttsController.speakInterruptingly("Apply Sharpen filter")
                                    isFilterApplied =
                                        if (isSobelApplied || isUnsharpMaskApplied) true
                                        else !isFilterApplied
                                    isSharpenApplied = !isSharpenApplied
                                    isSobelApplied = false
                                    isUnsharpMaskApplied = false
                                    filteredImageBitmap = sharpenImageBitmap
                                },
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .height(65.dp)
                                    .width(65.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if(!isSharpenApplied) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = if (!isSharpenApplied) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_filter_2_24),
                                    contentDescription = "Apply or Undo Sharpen Filter"
                                )
                            }
                        }
                        if (unsharpMaskImageBitmap != null) {
                            IconButton(
                                onClick = {
                                    if (isUnsharpMaskApplied) ttsController.speakInterruptingly(
                                        "Remove Unsharp mask filter"
                                    )
                                    else ttsController.speakInterruptingly("Apply Unsharp mask filter")
                                    isFilterApplied =
                                        if (isSobelApplied || isSharpenApplied) true
                                        else !isFilterApplied
                                    isUnsharpMaskApplied = !isUnsharpMaskApplied
                                    isSobelApplied = false
                                    isSharpenApplied = false
                                    filteredImageBitmap = unsharpMaskImageBitmap
                                },
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .height(65.dp)
                                    .width(65.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if(!isUnsharpMaskApplied) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = if (!isUnsharpMaskApplied) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_filter_3_24),
                                    contentDescription = "Apply or Undo Unsharp Mask Filter"
                                )
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = {
                            if (isFilterApplied) {
                                saveImageToGallery(filteredImageBitmap!!.asAndroidBitmap(), context)
                                Toast.makeText(
                                    context,
                                    "Filtered photo saved to gallery",
                                    Toast.LENGTH_SHORT
                                ).show()
                                ttsController.speakInterruptingly("Filtered photo saved to gallery")
                            } else {
                                saveImageToGallery(capturedImageBitmap!!.asAndroidBitmap(), context)
                                Toast.makeText(
                                    context,
                                    "Photo saved to gallery",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                ttsController.speakInterruptingly("Photo saved to gallery")
                            }
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .height(80.dp)
                            .width(80.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_gallery),
                            contentDescription = "Save to Gallery"
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = {
                            ttsController.speakInterruptingly("Reset")
                            filteredImageBitmap = null
                            capturedImageBitmap = null
                            sobelImageBitmap = null
                            sharpenImageBitmap = null
                            unsharpMaskImageBitmap = null
                            isFrozen = false
                            isFilterApplied = false
                            isSobelApplied = false
                            isSharpenApplied = false
                            isUnsharpMaskApplied = false
                        }, modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .height(80.dp)
                            .width(80.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                            contentDescription = "Reset magnifier"
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = { showFilterButtons = !showFilterButtons }, modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .height(80.dp)
                            .width(80.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if(!showFilterButtons) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = if (!showFilterButtons) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_filter_list_24),
                            contentDescription = "Filters"
                        )
                    }
                }
            }
        } else {
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
        if (!isFrozen) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(40.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Torch(controller, ttsController)
                    Spacer(modifier = Modifier.width(20.dp))
                    IconButton(
                        onClick = {
                            ttsController.speakInterruptingly("Freeze")
                            controller.takePicture(
                                cameraExecutor,
                                object : ImageCapture.OnImageCapturedCallback() {
                                    override fun onCaptureSuccess(image: ImageProxy) {
                                        Log.d("CameraPreview", "Image captured successfully")
                                        super.onCaptureSuccess(image)
                                        val imageBitmap = convertImageProxyToBitmap(image)
                                        capturedImageBitmap = imageBitmap
                                        isFrozen = true
                                        image.close()

                                        if (imageBitmap != null) {
                                            scope.launch(Dispatchers.Default) {
                                                val sobelImage = applySobelFilter(imageBitmap)
                                                val sharpenImage = applySharpenFilter(imageBitmap)
                                                val unsharpMaskImage =
                                                    applyUnsharpMaskFilter(imageBitmap)
                                                withContext(Dispatchers.Main) {
                                                    sobelImageBitmap = sobelImage
                                                    sharpenImageBitmap = sharpenImage
                                                    unsharpMaskImageBitmap = unsharpMaskImage
                                                }
                                            }
                                        }
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Log.e(
                                            "CameraPreview",
                                            "Image capture error: ${exception.message}",
                                            exception
                                        )
                                    }
                                })
                        }, modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .height(80.dp)
                            .width(80.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_freeze),
                            contentDescription = "Freeze"
                        )
                    }
                }
            }
        }
    }
}