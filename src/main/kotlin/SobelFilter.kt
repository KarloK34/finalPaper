import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val inputImagePath = "src/main/resources/images/eye.jpeg"
    val inputImagePath2 = "src/main/resources/images/lion.jpeg"
    val inputImagePath3 = "src/main/resources/images/machine.png"
    val inputImagePath4 = "src/main/resources/images/test.png"
    val outputImagePath = "src/main/resources/images/output_image3.jpeg"

    val inputImage = ImageIO.read(File(inputImagePath))

    val sobelImage = applySobelFilter(inputImage)

    ImageIO.write(sobelImage, "jpg", File(outputImagePath))

    println("Sobel filter applied successfully. Output image saved to: $outputImagePath")
}

private fun applySobelFilter(inputImage: BufferedImage): BufferedImage {
    val width = inputImage.width
    val height = inputImage.height

    val outputImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

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
                    val pixel = inputImage.getRGB(x + j, y + i)
                    val red = (pixel shr 16) and 0xFF
                    val green = (pixel shr 8) and 0xFF
                    val blue = pixel and 0xFF

                    gradientXRed += red * sobelX[i + 1][j + 1]
                    gradientYRed += red * sobelY[i + 1][j + 1]

                    gradientXGreen += green * sobelX[i + 1][j + 1]
                    gradientYGreen += green * sobelY[i + 1][j + 1]

                    gradientXBlue += blue * sobelX[i + 1][j + 1]
                    gradientYBlue += blue * sobelY[i + 1][j + 1]
                }
            }

            val magnitudeRed = Math.sqrt((gradientXRed * gradientXRed + gradientYRed * gradientYRed).toDouble()).toInt()
            val magnitudeGreen = Math.sqrt((gradientXGreen * gradientXGreen + gradientYGreen * gradientYGreen).toDouble()).toInt()
            val magnitudeBlue = Math.sqrt((gradientXBlue * gradientXBlue + gradientYBlue * gradientYBlue).toDouble()).toInt()

            val clampedMagnitudeRed = Math.min(255, Math.max(0, magnitudeRed))
            val clampedMagnitudeGreen = Math.min(255, Math.max(0, magnitudeGreen))
            val clampedMagnitudeBlue = Math.min(255, Math.max(0, magnitudeBlue))

            val outputPixel = (clampedMagnitudeRed shl 16) or (clampedMagnitudeGreen shl 8) or clampedMagnitudeBlue
            outputImage.setRGB(x, y, outputPixel)
        }
    }

    return outputImage
}
