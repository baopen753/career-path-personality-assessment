package org.swd392.users.event.producer;

public interface EventProducer<T> {
    void sendMessage(T event);
}
