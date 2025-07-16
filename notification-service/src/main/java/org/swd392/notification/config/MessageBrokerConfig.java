package org.swd392.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageBrokerConfig {

    @Value("${rabbitmq.seminar.exchange}")
    private String seminarExchange;

    @Value("${rabbitmq.seminar.queue}")
    private String seminarQueue;

    @Value("${rabbitmq.seminar.routing-key}")
    private String seminarRoutingKey;

    @Value("${rabbitmq.user.exchange}")
    private String userExchange;

    @Value("${rabbitmq.user.queue}")
    private String userQueue;

    @Value("${rabbitmq.user.routing-key}")
    private String userRoutingKey;


    @Bean
    public DirectExchange seminarDirectExchange() {
        return new DirectExchange(seminarExchange);
    }

    @Bean
    public Queue seminarQueue() {
        return new Queue(seminarQueue);
    }

    @Bean
    public Binding seminarBinding() {
        return BindingBuilder.bind(seminarQueue())
                .to(seminarDirectExchange())
                .with(seminarRoutingKey);
    }


    @Bean
    public DirectExchange userDirectExchange() {
        return new DirectExchange(userExchange);
    }

    @Bean
    public Queue userQueue() {
        return new Queue(userQueue);
    }

    @Bean
    public Binding userBinding() {
        return BindingBuilder.bind(userQueue())
                .to(userDirectExchange())
                .with(userRoutingKey);
    }


    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }


}
