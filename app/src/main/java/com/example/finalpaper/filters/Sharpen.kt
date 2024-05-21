package com.example.finalpaper.filters

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

fun applySharpenFilter(originalImage: ImageBitmap): ImageBitmap {
    Log.d("Test", "Pocetak")
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
    return runBlocking { applyConvolutionParallel(inputBitmap, kernel).asImageBitmap() }
}

suspend fun applyConvolutionParallel(inputBitmap: Bitmap, kernel: Array<DoubleArray>): Bitmap {
    var resultBitmap: Bitmap
    coroutineScope {
        val width = inputBitmap.width
        val height = inputBitmap.height
        resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val jobs = (1 until width - 1).map { x ->
            async(Dispatchers.Default) {
                IntArray(height - 2) { y ->
                    val red = applyConvolutionToChannel(inputBitmap, x, y + 1, kernel, 'r')
                    val green = applyConvolutionToChannel(inputBitmap, x, y + 1, kernel, 'g')
                    val blue = applyConvolutionToChannel(inputBitmap, x, y + 1, kernel, 'b')
                    Color.rgb(red, green, blue)
                }
            }
        }

        val rows = jobs.awaitAll()
        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                resultBitmap.setPixel(x, y, rows[x - 1][y - 1])
            }
        }
    }
    return resultBitmap
}
