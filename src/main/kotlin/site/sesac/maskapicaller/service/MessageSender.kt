package site.sesac.maskapicaller.service
import mu.KotlinLogging
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
private val logger = KotlinLogging.logger {}
@Service
class MessageSender(private val rabbitTemplate: RabbitTemplate) {

    fun sendMessage(message : String, que: String) {
        try {
            rabbitTemplate.convertAndSend(que,  message)
            logger.info { "MQ Message Produce done" }
        }
        catch (e: Exception){
            logger.warn { "MQ Message Produce fail" }
            throw e

        }
    }
}