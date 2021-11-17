package org.myalerts.event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.myalerts.marker.ThreadSafe;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventBroadcaster {

    private static final Executor EVENT_THREAD = new ThreadPoolExecutor(1, 2, 30L, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(5000), new ThreadPoolExecutor.CallerRunsPolicy());

    private static final Map<Class<? extends Event>, List<Consumer<Event>>> CONSUMERS = new HashMap<>();

    @ThreadSafe
    public static synchronized void register(final Consumer<Event> consumer, final Class<? extends Event> acceptedEvent) {
        List<Consumer<Event>> consumers = CONSUMERS.get(acceptedEvent);
        if (consumers == null) {
            consumers = new LinkedList<>();
            consumers.add(consumer);
            CONSUMERS.put(acceptedEvent, consumers);
        } else {
            consumers.add(consumer);
        }

        log.info("A new broadcast consumer is registered. The total number of consumers for event type '{}' is {}.", acceptedEvent.getName(), consumers.size());
    }

    @ThreadSafe
    public static synchronized void broadcast(final Event event) {
        Optional.ofNullable(CONSUMERS.get(event.getClass()))
            .orElse(List.of())
            .forEach(consumer -> EVENT_THREAD.execute(() -> consumer.accept(event)));
    }

}
