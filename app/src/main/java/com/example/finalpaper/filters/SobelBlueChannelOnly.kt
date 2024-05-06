package com.example.finalpaper.filters

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlin.math.sqrt

fun applySobelBlueChannelFilter(image: ImageBitmap): ImageBitmap {
    Log.d("Test", "Pocetak")

    val inputBitmap = image.asAndroidBitmap()

    val width = inputBitmap.width
    val height = inputBitmap.height

    val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val sobelX = arrayOf(intArrayOf(-1, 0, 1), intArrayOf(-2, 0, 2), intArrayOf(-1, 0, 1))
    val sobelY = arrayOf(intArrayOf(-1, -2, -1), intArrayOf(0, 0, 0), intArrayOf(1, 2, 1))

    for (y in 1 until height - 1) {
        for (x in 1 until width - 1) {
            var gradientX = 0
            var gradientY = 0

            for (i in -1..1) {
                for (j in -1..1) {
                    val pixel = inputBitmap.getPixel(x + j, y + i) and 0xFF
                    gradientX += pixel * sobelX[i + 1][j + 1]
                    gradientY += pixel * sobelY[i + 1][j + 1]
                }
            }

            val magnitude = sqrt((gradientX * gradientX + gradientY * gradientY).toDouble()).toInt()

            val clampedMagnitude = 255.coerceAtMost(0.coerceAtLeast(magnitude))

            val outputPixel = (clampedMagnitude shl 16) or (clampedMagnitude shl 8) or clampedMagnitude
            outputBitmap.setPixel(x, y, outputPixel)
        }
    }
    Log.d("Test", "Kraj")

    return outputBitmap.asImageBitmap()
}