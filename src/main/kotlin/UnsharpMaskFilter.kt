import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.exp
import kotlin.math.roundToInt

fun main() {
    val image1 = "src/main/resources/images/eye.jpeg"
    val image2 = "src/main/resources/images/test.png"
    val image3 = "src/main/resources/images/face.jpg"
    val image4 = "src/main/resources/images/lion.jpeg"
    val outputImagePath = "src/main/resources/images/output_image.jpeg"
    val originalImage = ImageIO.read(File(image4))

    if (originalImage != null) {
        val kernelSize = 5

        val gaussianKernel = createGaussianKernel(kernelSize)

        val sharpenedImage = applyUnsharpMask(originalImage, gaussianKernel, 1.0)

        ImageIO.write(sharpenedImage, "jpg", File(outputImagePath))
    } else {
        println("Failed to load the image.")
    }
}

private fun applyUnsharpMask(inputImage: BufferedImage, kernel: Array<DoubleArray>, strength: Double): BufferedImage {
    val blurredImage = applyConvolution(inputImage, kernel)

    val width = inputImage.width
    val height = inputImage.height
    val resultImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    for (x in 0 until width) {
        for (y in 0 until height) {
            val originalColor = Color(inputImage.getRGB(x, y))
            val blurredColor = Color(blurredImage.getRGB(x, y))

            val highPassRed = (originalColor.red - blurredColor.red).coerceIn(0, 255)
            val highPassGreen = (originalColor.green - blurredColor.green).coerceIn(0, 255)
            val highPassBlue = (originalColor.blue - blurredColor.blue).coerceIn(0, 255)

            val sharpenedRed = (originalColor.red + strength * highPassRed).roundToInt().coerceIn(0, 255)
            val sharpenedGreen = (originalColor.green + strength * highPassGreen).roundToInt().coerceIn(0, 255)
            val sharpenedBlue = (originalColor.blue + strength * highPassBlue).roundToInt().coerceIn(0, 255)

            resultImage.setRGB(x, y, Color(sharpenedRed, sharpenedGreen, sharpenedBlue).rgb)
        }
    }

    return resultImage
}

private fun createGaussianKernel(size: Int): Array<DoubleArray> {
    val kernel = Array(size) { DoubleArray(size) }
    val sigma = size / 10.0
    val twoSigmaSquared = 2 * sigma * sigma
    var sum = 0.0

    for (i in 0 until size) {
        for (j in 0 until size) {
            val x = i - size / 2
            val y = j - size / 2
            kernel[i][j] = exp(-(x * x + y * y) / twoSigmaSquared) / (Math.PI * twoSigmaSquared)
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

private fun applyConvolution(inputImage: BufferedImage, kernel: Array<DoubleArray>): BufferedImage {
    val width = inputImage.width
    val height = inputImage.height
    val resultImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    for (x in 1 until width - 1) {
        for (y in 1 until height - 1) {
            val red = applyConvolutionToChannel(inputImage, x, y, kernel, 'r')
            val green = applyConvolutionToChannel(inputImage, x, y, kernel, 'g')
            val blue = applyConvolutionToChannel(inputImage, x, y, kernel, 'b')

            resultImage.setRGB(x, y, Color(red, green, blue).rgb)
        }
    }

    return resultImage
}

private fun applyConvolutionToChannel(
    inputImage: BufferedImage,
    x: Int,
    y: Int,
    kernel: Array<DoubleArray>,
    channel: Char
): Int {
    var sum = 0.0

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

    return sum.coerceIn(0.0, 255.0).toInt()
}



