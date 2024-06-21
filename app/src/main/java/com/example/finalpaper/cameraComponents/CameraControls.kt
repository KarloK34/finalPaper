package com.example.finalpaper.cameraComponents

import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.finalpaper.R
import com.example.finalpaper.audioUtilities.TextToSpeechController
import com.example.finalpaper.cameraUtilities.convertImageProxyToBitmap
import java.util.concurrent.Executor

@Composable
fun CameraControls(
    controller: LifecycleCameraController,
    ttsController: TextToSpeechController,
    cameraExecutor: Executor,
    onCaptureImage: (ImageBitmap) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(40.dp),
        verticalArrangement = Arrangement.Bottom
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
                                super.onCaptureSuccess(image)
                                val imageBitmap = convertImageProxyToBitmap(image)
                                if (imageBitmap != null) {
                                    onCaptureImage(imageBitmap)
                                }
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
                    painter = painterResource(id = R.drawable.ic_freeze),
                    contentDescription = "Freeze"
                )
            }
        }
    }
}
