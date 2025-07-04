package org.swd392.notification.event.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.swd392.notification.event.EventListener;
import org.swd392.notification.event.TicketBookedEvent;

@Slf4j
@Service
public class TicketBookedListener implements EventListener<TicketBookedEvent> {

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    @Override
    public void consume(TicketBookedEvent event) {
        log.info("TicketBookedEvent received: {}", event);
        // LOG INFO: TicketBookedEvent received: TicketBookedEvent(userId=7, email=[user-email], fullName=...., paymentOrderCode=1751594894132, status=COMPLETED, createdAt=2025-07-04T10:38:09.389508823)

        // TODO: gửi email thông email dưới đây đi kèm thông tin liên quan đến transaction


    }
}
