package com.example.finalpaper.filters

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlin.math.sqrt

fun applySobelFilter(image: ImageBitmap): ImageBitmap {
    Log.d("Test", "Pocetak")
    val inputBitmap = image.asAndroidBitmap()
    val width = inputBitmap.width
    val height = inputBitmap.height

    val inputPixels = IntArray(width * height)
    inputBitmap.getPixels(inputPixels, 0, width, 0, 0, width, height)

    val outputPixels = IntArray(width * height)

    val sobelX = arrayOf(intArrayOf(-1, 0, 1), intArrayOf(-2, 0, 2), intArrayOf(-1, 0, 1))
    val sobelY = arrayOf(intArrayOf(-1, -2, -1), intArrayOf(0, 0, 0), intArrayOf(1, 2, 1))

    for (y in 1 until height - 1) {
        for (x in 1 until width - 1) {
            var gradientXRed = 0
            var gradientYRed = 0
            var gradientXGreen = 0
            var gradientYGreen = 0
            var gradientXBlue = 0
            var gradientYBlue = 0

            for (i in -1..1) {
                for (j in -1..1) {
                    val index = (y + i) * width + (x + j)
                    val pixel = inputPixels[index]
                    val red = Color.red(pixel)
                    val green = Color.green(pixel)
                    val blue = Color.blue(pixel)

                    gradientXRed += red * sobelX[i + 1][j + 1]
                    gradientYRed += red * sobelY[i + 1][j + 1]

                    gradientXGreen += green * sobelX[i + 1][j + 1]
                    gradientYGreen += green * sobelY[i + 1][j + 1]

                    gradientXBlue += blue * sobelX[i + 1][j + 1]
                    gradientYBlue += blue * sobelY[i + 1][j + 1]
                }
            }

            val magnitudeRed = sqrt((gradientXRed * gradientXRed + gradientYRed * gradientYRed).toDouble()).toInt()
            val magnitudeGreen = sqrt((gradientXGreen * gradientXGreen + gradientYGreen * gradientYGreen).toDouble()).toInt()
            val magnitudeBlue = sqrt((gradientXBlue * gradientXBlue + gradientYBlue * gradientYBlue).toDouble()).toInt()

            val clampedMagnitudeRed = magnitudeRed.coerceIn(0, 255)
            val clampedMagnitudeGreen = magnitudeGreen.coerceIn(0, 255)
            val clampedMagnitudeBlue = magnitudeBlue.coerceIn(0, 255)

            outputPixels[y * width + x] = Color.rgb(clampedMagnitudeRed, clampedMagnitudeGreen, clampedMagnitudeBlue)
        }
    }
    Log.d("Test", "Kraj")

    val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    outputBitmap.setPixels(outputPixels, 0, width, 0, 0, width, height)

    return outputBitmap.asImageBitmap()
}