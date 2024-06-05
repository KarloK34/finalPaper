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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.example.finalpaper.filters.applySobelFilter
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
    val cameraExecutor = ContextCompat.getMainExecutor(context)
    var isFrozen by remember { mutableStateOf(false) }
    var isFilterApplied by remember {
        mutableStateOf(false)
    }

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
            Button(
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
                        Toast.makeText(context, "Photo saved to gallery", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text("Save to Gallery")
            }
            if (filteredImageBitmap != null) {
                Button(
                    onClick = { isFilterApplied = !isFilterApplied },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    if (isFilterApplied) {
                        Text(text = "Undo filter")
                    } else {
                        Text(text = "Apply filter")
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
                                                val filteredImage = applySobelFilter(imageBitmap)
                                                withContext(Dispatchers.Main) {
                                                    filteredImageBitmap = filteredImage
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