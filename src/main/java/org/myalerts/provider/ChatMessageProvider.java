package org.myalerts.provider;

import java.util.stream.Stream;

import com.vaadin.collaborationengine.CollaborationMessage;
import com.vaadin.collaborationengine.CollaborationMessagePersister;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageProvider implements CollaborationMessagePersister {

    @Override
    public Stream<CollaborationMessage> fetchMessages(final FetchQuery fetchQuery) {
        return null;
    }

    @Override
    public void persistMessage(final PersistRequest persistRequest) {

    }

}
