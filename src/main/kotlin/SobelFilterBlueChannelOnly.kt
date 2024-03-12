import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val inputImagePath = "src/main/resources/images/eye.jpeg"
    val inputImagePath2 = "src/main/resources/images/lion.jpeg"
    val inputImagePath3 = "src/main/resources/images/machine.png"
    val inputImagePath4 = "src/main/resources/images/test.png"
    val outputImagePath = "src/main/resources/images/output_image4.jpeg"

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
            var gradientX = 0
            var gradientY = 0

            for (i in -1..1) {
                for (j in -1..1) {
                    val pixel = inputImage.getRGB(x + j, y + i) and 0xFF
                    gradientX += pixel * sobelX[i + 1][j + 1]
                    gradientY += pixel * sobelY[i + 1][j + 1]
                }
            }

            val magnitude = Math.sqrt((gradientX * gradientX + gradientY * gradientY).toDouble()).toInt()

            val clampedMagnitude = Math.min(255, Math.max(0, magnitude))

            val outputPixel = (clampedMagnitude shl 16) or (clampedMagnitude shl 8) or clampedMagnitude
            outputImage.setRGB(x, y, outputPixel)
        }
    }

    return outputImage
}

