package com.example.finalpaper.cameraUtilities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.ImageProxy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

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