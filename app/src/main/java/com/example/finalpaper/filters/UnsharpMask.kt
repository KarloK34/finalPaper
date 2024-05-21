package com.example.finalpaper.filters

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
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
    return applyUnsharpMask(inputBitmap, gaussianKernel, strength).asImageBitmap()
}

fun applyUnsharpMask(inputBitmap: Bitmap, kernel: Array<DoubleArray>, strength: Double): Bitmap {
    val blurredBitmap = applyConvolution(inputBitmap, kernel)

    val width = inputBitmap.width
    val height = inputBitmap.height
    val resultPixels = IntArray(width * height)

    val inputPixels = IntArray(width * height)
    inputBitmap.getPixels(inputPixels, 0, width, 0, 0, width, height)

    val blurredPixels = IntArray(width * height)
    blurredBitmap.getPixels(blurredPixels, 0, width, 0, 0, width, height)

    for (i in 0 until width * height) {
        val originalColor = inputPixels[i]
        val blurredColor = blurredPixels[i]

        val highPassRed = (Color.red(originalColor) - Color.red(blurredColor)).coerceIn(0, 255)
        val highPassGreen = (Color.green(originalColor) - Color.green(blurredColor)).coerceIn(0, 255)
        val highPassBlue = (Color.blue(originalColor) - Color.blue(blurredColor)).coerceIn(0, 255)

        val sharpenedRed = (Color.red(originalColor) + strength * highPassRed).roundToInt().coerceIn(0, 255)
        val sharpenedGreen = (Color.green(originalColor) + strength * highPassGreen).roundToInt().coerceIn(0, 255)
        val sharpenedBlue = (Color.blue(originalColor) + strength * highPassBlue).roundToInt().coerceIn(0, 255)

        resultPixels[i] = Color.rgb(sharpenedRed, sharpenedGreen, sharpenedBlue)
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

fun applyConvolution(inputBitmap: Bitmap, kernel: Array<DoubleArray>): Bitmap {
    val width = inputBitmap.width
    val height = inputBitmap.height
    val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    for (x in 1 until width - 1) {
        for (y in 1 until height - 1) {
            val red = applyConvolutionToChannel(inputBitmap, x, y, kernel, 'r')
            val green = applyConvolutionToChannel(inputBitmap, x, y, kernel, 'g')
            val blue = applyConvolutionToChannel(inputBitmap, x, y, kernel, 'b')

            resultBitmap.setPixel(x, y, Color.rgb(red, green, blue))
        }
    }
    Log.d("Test", "Kraj")
    return resultBitmap
}

fun applyConvolutionToChannel(
    inputBitmap: Bitmap,
    x: Int,
    y: Int,
    kernel: Array<DoubleArray>,
    channel: Char
): Int {
    var sum = 0.0

    for (i in -1..1) {
        for (j in -1..1) {
            val pixelValue = when (channel) {
                'r' -> Color.red(inputBitmap.getPixel(x + i, y + j))
                'g' -> Color.green(inputBitmap.getPixel(x + i, y + j))
                'b' -> Color.blue(inputBitmap.getPixel(x + i, y + j))
                else -> throw IllegalArgumentException("Invalid channel")
            }
            sum += pixelValue * kernel[i + 1][j + 1]
        }
    }

    return sum.coerceIn(0.0, 255.0).toInt()
}

fun downsampleImage(image: ImageBitmap, targetWidth: Int, targetHeight: Int): Bitmap {
    val inputBitmap = image.asAndroidBitmap()
    val width = inputBitmap.width
    val height = inputBitmap.height

    val matrix = Matrix()
    matrix.postScale(targetWidth.toFloat() / width, targetHeight.toFloat() / height)

    return Bitmap.createBitmap(inputBitmap, 0, 0, width, height, matrix, true)
}