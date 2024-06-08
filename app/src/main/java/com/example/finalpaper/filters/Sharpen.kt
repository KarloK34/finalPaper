package com.example.finalpaper.filters

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun applySharpenFilter(originalImage: ImageBitmap): ImageBitmap {
    val kernel = arrayOf(
        doubleArrayOf(0.0, -1.0, 0.0),
        doubleArrayOf(-1.0, 5.0, -1.0),
        doubleArrayOf(0.0, -1.0, 0.0)
    )
    val originalWidth = originalImage.width
    val originalHeight = originalImage.height
    val targetWidth = originalWidth / 2
    val targetHeight = originalHeight / 2
    val inputBitmap = downsampleImage(originalImage, targetWidth, targetHeight)
    return applyConvolutionParallel(inputBitmap, kernel).asImageBitmap()
}

suspend fun applyConvolutionParallel(inputBitmap: Bitmap, kernel: Array<DoubleArray>): Bitmap {
    val width = inputBitmap.width
    val height = inputBitmap.height
    val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val inputPixels = IntArray(width * height)
    inputBitmap.getPixels(inputPixels, 0, width, 0, 0, width, height)
    val resultPixels = IntArray(width * height)

    coroutineScope {
        val chunkSize = height / 4
        val jobs = (0 until height step chunkSize).map { startY ->
            async(Dispatchers.Default) {
                for (y in startY until (startY + chunkSize).coerceAtMost(height)) {
                    for (x in 0 until width) {
                        if (x > 0 && x < width - 1 && y > 0 && y < height - 1) {
                            val red = applyConvolutionToChannel(inputPixels, width, x, y, kernel, 'r')
                            val green = applyConvolutionToChannel(inputPixels, width, x, y, kernel, 'g')
                            val blue = applyConvolutionToChannel(inputPixels, width, x, y, kernel, 'b')
                            resultPixels[y * width + x] = Color.rgb(red, green, blue)
                        } else {
                            resultPixels[y * width + x] = inputPixels[y * width + x]
                        }
                    }
                }
            }
        }
        jobs.awaitAll()
    }

    resultBitmap.setPixels(resultPixels, 0, width, 0, 0, width, height)
    return resultBitmap
}
fun applyConvolutionToChannel(
    pixels: IntArray,
    width: Int,
    x: Int,
    y: Int,
    kernel: Array<DoubleArray>,
    channel: Char
): Int {
    var sum = 0.0

    for (i in -1..1) {
        for (j in -1..1) {
            val pixelIndex = (y + j) * width + (x + i)
            val pixelValue = when (channel) {
                'r' -> Color.red(pixels[pixelIndex])
                'g' -> Color.green(pixels[pixelIndex])
                'b' -> Color.blue(pixels[pixelIndex])
                else -> throw IllegalArgumentException("Invalid channel")
            }
            sum += pixelValue * kernel[i + 1][j + 1]
        }
    }

    return sum.coerceIn(0.0, 255.0).toInt()
}