package org.swd392.seminars.event.producer.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.swd392.seminars.event.SeminarApprovedEvent;
import org.swd392.seminars.event.producer.EventProducer;

@Slf4j
@Service
public class SeminarApprovedEventProducer implements EventProducer<SeminarApprovedEvent> {

    @Value("${rabbitmq.seminar-approved.exchange}")
    private String exchange;

    @Value("${rabbitmq.seminar-approved.routing-key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public SeminarApprovedEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendMessage(SeminarApprovedEvent event) {
        log.info("Sending seminar approved event to message broker: {}", event);
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
} 