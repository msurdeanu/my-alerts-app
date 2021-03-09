package org.myalerts.app.event;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface EventListener<T extends Event> {

    void onEventReceived(T event);

    Class<T> getEventType();

}
