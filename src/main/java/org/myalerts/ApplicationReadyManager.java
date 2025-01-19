package org.myalerts;

import lombok.RequiredArgsConstructor;
import org.myalerts.domain.event.Event;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class ApplicationReadyManager {

    private final ApplicationManager applicationManager;

    @EventListener
    @Order(1)
    public void registerAllListeners(ApplicationReadyEvent event) {
        event.getApplicationContext()
            .getBeansOfType(org.myalerts.domain.event.EventListener.class)
            .forEach(this::registerListener);
    }

    private void registerListener(String key, org.myalerts.domain.event.EventListener<Event> value) {
        applicationManager.getBeanOfType(EventBroadcaster.class).register(value::onEventReceived, value.getEventType());
    }

}
