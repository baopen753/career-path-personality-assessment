package org.swd392.notification.event;

public interface EventListener<T> {
    void consume(T event);
}
