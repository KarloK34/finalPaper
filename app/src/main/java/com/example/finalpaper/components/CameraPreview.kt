package com.example.finalpaper.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.finalpaper.R
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var capturedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val cameraExecutor = Executors.newSingleThreadExecutor()
    var isFrozen by remember { mutableStateOf(false) }

    Box {
        if (capturedImageBitmap != null && isFrozen) {
            Image(
                bitmap = capturedImageBitmap!!,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = { isFrozen = false }, modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        color = Color.LightGray
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back_arrow),
                    contentDescription = "Go Back"
                )
            }
            Button(onClick = { /*TODO*/ }, modifier = Modifier.align(Alignment.BottomEnd)) {
                Text(text = "Apply filter")
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
                        .height(30.dp)
                ) {
                    Torch(controller)
                    Spacer(modifier = Modifier.width(5.dp))
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

fun convertImageProxyToBitmap(imageProxy: ImageProxy): ImageBitmap? {
    val planeProxy = imageProxy.planes[0]
    val buffer = planeProxy.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    val rotationDegrees = when (imageProxy.imageInfo.rotationDegrees) {
        0 -> 0f
        90 -> 90f
        180 -> 180f
        270 -> 270f
        else -> 0f
    }

    val rotatedBitmap = rotateBitmap(bitmap, rotationDegrees)

    return rotatedBitmap?.asImageBitmap()
}

private fun rotateBitmap(bitmap: Bitmap?, degrees: Float): Bitmap? {
    bitmap ?: return null
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}