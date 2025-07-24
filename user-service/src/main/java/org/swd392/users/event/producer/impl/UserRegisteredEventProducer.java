package org.swd392.users.event.producer.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.swd392.users.event.UserRegisteredEvent;
import org.swd392.users.event.producer.EventProducer;

@Slf4j
@Service
public class UserRegisteredEventProducer implements EventProducer<UserRegisteredEvent> {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public UserRegisteredEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendMessage(UserRegisteredEvent event) {
        log.info("Sending user registered event to message broker: {}", event);
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
