package org.swd392.notification.event.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.swd392.notification.event.EventListener;
import org.swd392.notification.event.UserRegisteredEvent;

@Slf4j
@Service
public class UserRegisteredListener implements EventListener<UserRegisteredEvent> {


    @RabbitListener(queues = "${rabbitmq.user.queue}")
    @Override
    public void consume(UserRegisteredEvent event) {

        log.info("UserRegisteredEvent received: {}", event);

        sendEmail();

    }
}
