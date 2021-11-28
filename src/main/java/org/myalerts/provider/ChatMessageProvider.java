package org.myalerts.provider;

import java.util.stream.Stream;

import com.google.common.collect.EvictingQueue;
import com.vaadin.collaborationengine.CollaborationMessage;
import com.vaadin.collaborationengine.CollaborationMessagePersister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Component
public class ChatMessageProvider implements CollaborationMessagePersister {

    public static final String TOPIC = "chat";

    private final EvictingQueue<CollaborationMessage> collaborationMessages;

    public ChatMessageProvider() {
        collaborationMessages = EvictingQueue.create(10);
    }

    @Override
    public Stream<CollaborationMessage> fetchMessages(final FetchQuery fetchQuery) {
        if (TOPIC.equals(fetchQuery.getTopicId())) {
            log.info("Test {}", fetchQuery.getSince());
            return collaborationMessages.stream();
        }

        return Stream.empty();
    }

    @Override
    public void persistMessage(final PersistRequest persistRequest) {
        collaborationMessages.add(persistRequest.getMessage());
    }

}
