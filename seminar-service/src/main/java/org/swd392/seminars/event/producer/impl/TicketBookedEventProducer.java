package org.swd392.seminars.event.producer.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.swd392.seminars.event.TicketBookedEvent;
import org.swd392.seminars.event.producer.EventProducer;

@Slf4j
@Service
public class TicketBookedEventProducer implements EventProducer<TicketBookedEvent> {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public TicketBookedEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendMessage(TicketBookedEvent ticketBookedEvent) {
        log.info("Sending ticket booked event to message broker: {}", ticketBookedEvent);
        rabbitTemplate.convertAndSend(exchange, routingKey, ticketBookedEvent);
    }
}
