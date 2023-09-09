package site.sesac.maskapicaller.service

import jakarta.annotation.PreDestroy
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import site.sesac.maskapicaller.config.AWSConfig
import software.amazon.awssdk.core.sync.RequestBody

import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

import java.util.*
import javax.imageio.ImageIO

private val logger = KotlinLogging.logger {}

@Service
class Base64EnDecoder(  awsConfig : AWSConfig
) {
    @Value("\${etc.s3bucket}")
    private lateinit var s3Bucket: String
    private val s3Client: S3Client = awsConfig.s3Client()


    fun encodeImage(imgName : String):String {

        return try {
            val getObjectRequest = GetObjectRequest.builder().bucket(s3Bucket)
                .key("""origin/${imgName}""").build()

            s3Client.getObject(getObjectRequest).use { getObjectResponse ->
                val imageBytes = getObjectResponse?.readAllBytes()
                val base64Image = Base64.getEncoder().encodeToString(imageBytes)
             //   logger.info { "Base64 encoded done" }
                base64Image
            }
        } catch (e: Exception) {
            e.printStackTrace()
   //         logger.error {"encoding error"}
            throw Exception()

        }

    }

    fun decodeImage(base64Image: String, s3Key :  String) {

        try {



            val imageBytes = Base64.getDecoder().decode(base64Image)

            val inputStream = ByteArrayInputStream(imageBytes)
            val originalImage = ImageIO.read(inputStream)
            val invertedImage = BufferedImage(originalImage.width, originalImage.height, originalImage.type)
            for (x in 0 until originalImage.width) {
                for (y in 0 until originalImage.height) {
                    val pixel = Color(originalImage.getRGB(x, y))
                    val invertedPixel = Color(255 - pixel.red, 255 - pixel.green, 255 - pixel.blue)
                    invertedImage.setRGB(x, y, invertedPixel.rgb)
                }
            }

            val outputStream = ByteArrayOutputStream()
            ImageIO.write(invertedImage, "jpg", outputStream)
            val invertedImageBytes = outputStream.toByteArray()
            val putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Bucket)
                .key("""mask/${s3Key}""")
                .contentType("image/png")
                .build()

            s3Client.putObject(putObjectRequest, RequestBody.fromByteBuffer(ByteBuffer.wrap(invertedImageBytes)))

            logger.info{"s3 upload done"}

        } catch (e: Exception) {
            logger.error {"decoding error"}
            e.printStackTrace()
            throw Exception()
        } finally {

        }
    }
    @PreDestroy
    fun onDestroy() {
        s3Client.close()
    }
}