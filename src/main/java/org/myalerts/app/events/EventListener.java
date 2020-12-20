package org.myalerts.app.events;

/**
 * @author Mihai Surdeanu
 * @param <T>
 * @since 1.0.0
 */
public interface EventListener<T extends Event> {

    void onEventReceived(T event);

    Class<T> getEventType();

}
