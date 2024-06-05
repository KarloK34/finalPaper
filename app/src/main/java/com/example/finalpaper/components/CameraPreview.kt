package com.example.finalpaper.components

import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    modifier: Modifier = Modifier
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
            ) {
                Row {
                    IconButton(
                        onClick = {
                            if (isFilterApplied) {
                                saveImageToGallery(filteredImageBitmap!!.asAndroidBitmap(), context)
                                Toast.makeText(
                                    context,
                                    "Filtered photo saved to gallery",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                saveImageToGallery(capturedImageBitmap!!.asAndroidBitmap(), context)
                                Toast.makeText(
                                    context,
                                    "Photo saved to gallery",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .height(55.dp)
                            .width(55.dp)
                            .background(
                                color = Color.LightGray
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
                            filteredImageBitmap = null
                            capturedImageBitmap = null
                            sobelImageBitmap = null
                            sharpenImageBitmap = null
                            unsharpMaskImageBitmap = null
                            isFrozen = false
                            isFilterApplied = false
                        }, modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .height(55.dp)
                            .width(55.dp)
                            .background(
                                color = Color.LightGray
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                            contentDescription = "Reset magnifier"
                        )
                    }
                    if (sobelImageBitmap != null) {
                        IconButton(
                            onClick = {
                                isFilterApplied =
                                    if(isSharpenApplied || isUnsharpMaskApplied) true
                                    else !isFilterApplied
                                isSobelApplied = !isSobelApplied
                                isUnsharpMaskApplied = false
                                isSharpenApplied = false
                                filteredImageBitmap = sobelImageBitmap
                            },
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .height(55.dp)
                                .width(55.dp)
                                .background(
                                    if (isSobelApplied) Color.Yellow else Color.LightGray
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
                                isFilterApplied =
                                    if(isSobelApplied || isUnsharpMaskApplied) true
                                    else !isFilterApplied
                                isSharpenApplied = !isSharpenApplied
                                isSobelApplied = false
                                isUnsharpMaskApplied = false
                                filteredImageBitmap = sharpenImageBitmap
                            },
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .height(55.dp)
                                .width(55.dp)
                                .background(
                                    if (isSharpenApplied) Color.Yellow else Color.LightGray
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
                                isFilterApplied =
                                    if(isSobelApplied || isSharpenApplied) true
                                    else !isFilterApplied
                                isUnsharpMaskApplied = !isUnsharpMaskApplied
                                isSobelApplied = false
                                isSharpenApplied = false
                                filteredImageBitmap = unsharpMaskImageBitmap
                            },
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .height(55.dp)
                                .width(55.dp)
                                .background(
                                    if (isUnsharpMaskApplied) Color.Yellow else Color.LightGray
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
                        .fillMaxWidth()
                ) {
                    Torch(controller)
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = {
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
                            .height(60.dp)
                            .width(60.dp)
                            .background(
                                color = Color.LightGray
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