package org.swd392.seminars;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.swd392.seminars.entity.SagaTransaction;
import org.swd392.seminars.event.TicketBookedEvent;
import org.swd392.seminars.event.producer.impl.TicketBookedEventProducer;

import java.time.LocalDateTime;

@SpringBootTest
class SeminarServiceApplicationTests {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Autowired
    private TicketBookedEventProducer eventProducer;

    @Test
    void testSendBookedTicketMessage_shouldNotThrowException() {
        TicketBookedEvent event = TicketBookedEvent.builder()
                .userId(11)
                .email("yourmail@gmail.com")
                .fullName("Tran Bao")
                .paymentOrderCode("1751594894132")
                .status(SagaTransaction.SagaStatus.COMPLETED.name())
                .createdAt(LocalDateTime.now())
                .build();

        eventProducer.sendMessage(event);
    }
}