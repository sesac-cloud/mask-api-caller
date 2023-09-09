package site.sesac.maskapicaller.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.*
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class AWSConfig(
    @Value("\${awskey.accessKeyId}")
    val accessKeyId: String,

    @Value("\${awskey.secretAccessKey}")
    val secretAccessKey: String
) {
    val region = Region.AP_NORTHEAST_2
    @Bean
    fun s3Client() : S3Client {
        return S3Client.builder().region(region).credentialsProvider(
            StaticCredentialsProvider
                .create(AwsBasicCredentials.create(accessKeyId,secretAccessKey)))
            .build()
    }

}