package site.sesac.maskapicaller.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import site.sesac.maskapicaller.data.MaskAPIMessage
private val logger = KotlinLogging.logger {}

@Component
class MessageListener(
                      private val apiCall: APICall,
                      private val messageSender: MessageSender,
                      private val dbDelete: DBDelete
    ) {

    //
    @RabbitListener(queues = ["mask"], containerFactory = "prefetchOneContainerFactory" )
    fun messageListener(message: org.springframework.amqp.core.Message) = try {

        val objectMapper = jacksonObjectMapper()
        val maskAPIMessage: MaskAPIMessage = objectMapper.readValue(String(message.body), MaskAPIMessage::class.java)
        logger.info { "${maskAPIMessage.userMail} : Request Get Message" }


        val resultPhotoName = apiCall.call(maskAPIMessage.originPhoto, maskAPIMessage.userMail)

        if (resultPhotoName == "402"){
            messageSender.sendMessage("""{"mail_type":"TokenException","user_mail":"hwanrim00@gmail.com"}""","mail")
            logger.info { "${maskAPIMessage.userMail} : 402 Process Done" }
        }
        else if (resultPhotoName != "error"){
            messageSender.sendMessage(
                """{"originPhoto":"${maskAPIMessage.originPhoto}","prompt":"${maskAPIMessage.prompt}","userMail":"${maskAPIMessage.userMail}","maskPhoto":"$resultPhotoName"}"""
                    .trimIndent(), "mix"
            )
            logger.info { "${maskAPIMessage.userMail} : Process Done" }
        }

        else {
            throw Exception()
        }
    }catch (e : Exception){
        logger.error { "Process error" }
    }

    @RabbitListener(queues = ["MA-DLQ"], containerFactory = "prefetchOneContainerFactory")
    fun dlqListener(message: org.springframework.amqp.core.Message) {
        val objectMapper = jacksonObjectMapper()
        val maskAPIMessage: MaskAPIMessage = objectMapper.readValue(String(message.body), MaskAPIMessage::class.java)
        logger.info { "${maskAPIMessage.userMail} : DLQ Get Message" }
       if( dbDelete.deleteOnFail(maskAPIMessage.userMail)) {
           messageSender.sendMessage("""{"mail_type":"F","user_mail":"${maskAPIMessage.userMail}"}""", "mail")
       }
    }


}