package org.example
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    // Load the image
    val inputImagePath = "src/main/resources/images/eye.jpeg"
    val outputImagePath = "src/main/resources/images/output_image2.jpeg"
    val originalImage = ImageIO.read(File(inputImagePath))

    if (originalImage != null) {
        val kernel = arrayOf(
            intArrayOf(0, -1, 0),
            intArrayOf(-1, 5, -1),
            intArrayOf(0, -1, 0)
        )

        // Apply convolution to the image
        val convolvedImage = applyConvolution(originalImage, kernel)

        // Save the result
        ImageIO.write(convolvedImage, "jpg", File(outputImagePath))
    } else {
        println("Failed to load the image.")
    }
}

internal fun applyConvolution(inputImage: BufferedImage, kernel: Array<IntArray>): BufferedImage {
    val width = inputImage.width
    val height = inputImage.height
    val resultImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    // Iterate over each pixel in the image
    for (x in 1 until width - 1) {
        for (y in 1 until height - 1) {
            // Apply convolution to each color channel separately (assuming RGB)
            val red = applyConvolutionToChannel(inputImage, x, y, kernel, 'r')
            val green = applyConvolutionToChannel(inputImage, x, y, kernel, 'g')
            val blue = applyConvolutionToChannel(inputImage, x, y, kernel, 'b')

            // Combine the results and set the pixel in the result image
            resultImage.setRGB(x, y, Color(red, green, blue).rgb)
        }
    }

    return resultImage
}

private fun applyConvolutionToChannel(
    inputImage: BufferedImage,
    x: Int,
    y: Int,
    kernel: Array<IntArray>,
    channel: Char
): Int {
    var sum = 0

    // Iterate over the kernel and calculate the convolution sum
    for (i in -1..1) {
        for (j in -1..1) {
            val pixelValue = when (channel) {
                'r' -> Color(inputImage.getRGB(x + i, y + j)).red
                'g' -> Color(inputImage.getRGB(x + i, y + j)).green
                'b' -> Color(inputImage.getRGB(x + i, y + j)).blue
                else -> throw IllegalArgumentException("Invalid channel")
            }
            sum += pixelValue * kernel[i + 1][j + 1]
        }
    }

    // Ensure the result is in the valid color range [0, 255]
    return sum.coerceIn(0, 255)
}
