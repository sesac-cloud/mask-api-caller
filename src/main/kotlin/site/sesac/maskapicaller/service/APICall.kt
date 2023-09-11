package site.sesac.maskapicaller.service

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import site.sesac.maskapicaller.data.MaskAPIResponse

private val logger = KotlinLogging.logger {}
@Service
class APICall(private val base64EnDecoder: Base64EnDecoder) {
    @Value("\${etc.apikey}")
    private lateinit var apikey: String

    fun call(originPhoto: String, userMail: String): String {

            val restTemplate = RestTemplate()
            val requestUrl = """https://api.remove.bg/v1.0/removebg""" // 실제 엔드포인트로 변경해야 함

            val requestHeaders = HttpHeaders()
            requestHeaders.contentType = MediaType.APPLICATION_JSON
            requestHeaders.set("Accept", "application/json")
            requestHeaders.set("X-Api-Key", apikey)
            val requestBody =
                """{
  "image_file_b64": "${base64EnDecoder.encodeImage(originPhoto)}",
  "image_url": "",
  "size": "preview",
  "type": "auto",
  "type_level": "1",
  "format": "auto",
  "roi": "0% 0% 100% 100%",
  "crop": false,
  "crop_margin": "0",
  "scale": "original",
  "position": "original",
  "channels": "alpha",
  "add_shadow": false,
  "semitransparency": true,
  "bg_color": "",
  "bg_image_url": ""
}""".trimIndent()
        try {
            val requestEntity = HttpEntity(requestBody, requestHeaders)


            val response = restTemplate.exchange(
                requestUrl,
                HttpMethod.POST,
                requestEntity,
                MaskAPIResponse::class.java
            )

         if ( response.statusCode != HttpStatus.OK) {
                logger.warn { "Request failed with status code: ${response.statusCode}" }
              return "error"
            }


            val resultPhotoName = "${userMail}_${System.currentTimeMillis()}_mask.jpg"
            response.body?.data?.resultB64?.let { base64EnDecoder.decodeImage(it, resultPhotoName) }

            return resultPhotoName

        }catch (e: HttpClientErrorException) {
            if(e.statusCode == HttpStatus.PAYMENT_REQUIRED) {
                logger.warn { "토큰 교체 필요" }
                return "402"
            }
            else{
                e.printStackTrace()
                throw Exception()
            }
        }
    }
}