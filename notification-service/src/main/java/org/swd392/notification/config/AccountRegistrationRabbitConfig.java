package org.swd392.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountRegistrationRabbitConfig {
    @Value("${rabbitmq.exchange.name:user.account-registration.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.queue.name:user.account-registration.queue}")
    private String queueName;

    @Bean(name = "accountRegistrationExchange")
    public DirectExchange accountRegistrationExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean(name = "accountRegistrationQueue")
    public Queue accountRegistrationQueue() {
        return new Queue(queueName);
    }

    @Bean(name = "accountRegistrationBinding")
    public Binding accountRegistrationBinding(Queue accountRegistrationQueue, DirectExchange accountRegistrationExchange) {
        return BindingBuilder.bind(accountRegistrationQueue).to(accountRegistrationExchange).with("user.account-registration");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
} 