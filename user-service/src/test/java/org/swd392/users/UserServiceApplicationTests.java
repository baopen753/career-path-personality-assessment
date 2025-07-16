package org.swd392.users;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.swd392.users.event.UserRegisteredEvent;
import org.swd392.users.event.producer.impl.UserRegisteredEventProducer;
import java.time.LocalDateTime;

@SpringBootTest
class UserServiceApplicationTests {


    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    @Autowired
    private UserRegisteredEventProducer eventProducer;

    @Test
    void testSendBookedTicketMessage_shouldNotThrowException() {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .email("youremail@gmail.com")
                .accountType("STUDENT")
                .registrationDate(LocalDateTime.now())
                .build();

        eventProducer.sendMessage(event);
    }

}
