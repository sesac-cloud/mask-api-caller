package site.sesac.maskapicaller.config

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class MQConfig {

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = jsonMessageConverter()
        return rabbitTemplate
    }

    @Bean
    fun jsonMessageConverter(): Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun prefetchOneContainerFactory(
        configurer: SimpleRabbitListenerContainerFactoryConfigurer, connectionFactory: ConnectionFactory?
    ): RabbitListenerContainerFactory<SimpleMessageListenerContainer> {
        val factory = SimpleRabbitListenerContainerFactory()
        configurer.configure(factory, connectionFactory)
        factory.setPrefetchCount(1)
        return factory
    }



    @Bean
    fun mailDeadLetterExchange(): FanoutExchange {
        return FanoutExchange("MAIL-DLX")
    }
    @Bean
    fun mixDeadLetterExchange(): FanoutExchange {
        return FanoutExchange("M-DLX")
    }
    @Bean
    fun maskDeadLetterExchange(): FanoutExchange {
        return FanoutExchange("MA-DLX")
    }

    @Bean
    fun mailDeadLetterQueue(): Queue {
        return QueueBuilder.durable("MAIL-DLQ").build()
    }
    @Bean
    fun mixDeadLetterQueue(): Queue {
        return QueueBuilder.durable("M-DLQ").build()
    }
    @Bean
    fun maskDeadLetterQueue(): Queue {
        return QueueBuilder.durable("MA-DLQ").build()
    }

    @Bean
    fun mixDeadLetterBinding(): Binding {
        return BindingBuilder.bind(mixDeadLetterQueue()).to(mixDeadLetterExchange())
    }
    @Bean
    fun mailLetterBinding(): Binding {
        return BindingBuilder.bind(mailDeadLetterQueue()).to(mailDeadLetterExchange())
    }
    @Bean
    fun maskDeadLetterBinding(): Binding {
        return BindingBuilder.bind(maskDeadLetterQueue()).to(maskDeadLetterExchange())
    }

    @Bean
    fun mailQueue(): Queue {
        return QueueBuilder.durable("mail")
            .withArgument("x-dead-letter-exchange", "MAIL-DLX")
            .build()
    }
    @Bean
    fun mixQueue(): Queue {
        return QueueBuilder.durable("mix")
            .withArgument("x-dead-letter-exchange", "M-DLX")
            .build()
    }
    @Bean
    fun maskQueue(): Queue {
        return QueueBuilder.durable("mask")
            .withArgument("x-dead-letter-exchange", "MA-DLX")
            .build()
    }


}