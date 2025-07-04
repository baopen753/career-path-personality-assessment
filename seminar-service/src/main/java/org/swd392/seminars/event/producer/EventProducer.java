package org.swd392.seminars.event.producer;

public interface EventProducer<T> {
    void sendMessage(T t);
}
