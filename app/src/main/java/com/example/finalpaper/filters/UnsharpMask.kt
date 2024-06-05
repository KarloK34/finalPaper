package com.example.finalpaper.filters

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun applyUnsharpMaskFilter(image: ImageBitmap): ImageBitmap {
    Log.d("Test", "Pocetak")
    val originalWidth = image.width
    val originalHeight = image.height
    val targetWidth = originalWidth / 2
    val targetHeight = originalHeight / 2
    val inputBitmap = downsampleImage(image,targetWidth,targetHeight)
    val kernelSize = 5
    val strength = 1.0

    val gaussianKernel = createGaussianKernel(kernelSize)
    return runBlocking { applyUnsharpMask(inputBitmap, gaussianKernel, strength).asImageBitmap() }
}
suspend fun applyUnsharpMask(inputBitmap: Bitmap, kernel: Array<DoubleArray>, strength: Double): Bitmap {
    val blurredBitmap = applyConvolutionParallel(inputBitmap, kernel)

    val width = inputBitmap.width
    val height = inputBitmap.height
    val resultPixels = IntArray(width * height)

    val inputPixels = IntArray(width * height)
    inputBitmap.getPixels(inputPixels, 0, width, 0, 0, width, height)

    val blurredPixels = IntArray(width * height)
    blurredBitmap.getPixels(blurredPixels, 0, width, 0, 0, width, height)

    coroutineScope {
        val chunkSize = height / 4
        val jobs = (0 until height step chunkSize).map { startY ->
            async(Dispatchers.Default) {
                for (y in startY until (startY + chunkSize).coerceAtMost(height)) {
                    for (x in 0 until width) {
                        val originalColor = inputPixels[y * width + x]
                        val blurredColor = blurredPixels[y * width + x]

                        val highPassRed = (Color.red(originalColor) - Color.red(blurredColor)).coerceIn(0, 255)
                        val highPassGreen = (Color.green(originalColor) - Color.green(blurredColor)).coerceIn(0, 255)
                        val highPassBlue = (Color.blue(originalColor) - Color.blue(blurredColor)).coerceIn(0, 255)

                        val sharpenedRed = (Color.red(originalColor) + strength * highPassRed).roundToInt().coerceIn(0, 255)
                        val sharpenedGreen = (Color.green(originalColor) + strength * highPassGreen).roundToInt().coerceIn(0, 255)
                        val sharpenedBlue = (Color.blue(originalColor) + strength * highPassBlue).roundToInt().coerceIn(0, 255)

                        resultPixels[y * width + x] = Color.rgb(sharpenedRed, sharpenedGreen, sharpenedBlue)
                    }
                }
            }
        }
        jobs.awaitAll()
    }

    val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    resultBitmap.setPixels(resultPixels, 0, width, 0, 0, width, height)

    Log.d("Test", "Kraj")
    return resultBitmap
}

fun createGaussianKernel(size: Int): Array<DoubleArray> {
    val kernel = Array(size) { DoubleArray(size) }
    val sigma = size / 10.0
    val twoSigmaSquared = 2 * sigma * sigma
    var sum = 0.0

    for (i in 0 until size) {
        for (j in 0 until size) {
            val x = i - size / 2
            val y = j - size / 2
            kernel[i][j] = exp(-(x * x + y * y) / twoSigmaSquared) / (sqrt(2*Math.PI) * sigma)
            sum += kernel[i][j]
        }
    }

    for (i in 0 until size) {
        for (j in 0 until size) {
            kernel[i][j] /= sum
        }
    }

    return kernel
}

fun downsampleImage(image: ImageBitmap, targetWidth: Int, targetHeight: Int): Bitmap {
    val inputBitmap = image.asAndroidBitmap()
    val width = inputBitmap.width
    val height = inputBitmap.height

    val matrix = Matrix()
    matrix.postScale(targetWidth.toFloat() / width, targetHeight.toFloat() / height)

    return Bitmap.createBitmap(inputBitmap, 0, 0, width, height, matrix, true)
}