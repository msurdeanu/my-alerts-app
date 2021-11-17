package org.myalerts.view;

import javax.annotation.security.PermitAll;

import com.vaadin.collaborationengine.CollaborationMessageList;
import com.vaadin.collaborationengine.PresenceManager;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

import org.myalerts.layout.BaseLayout;
import org.myalerts.layout.ResponsiveLayout;
import org.myalerts.service.SecurityService;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@PermitAll
@Route(value = ChatView.ROUTE, layout = BaseLayout.class)
public class ChatView extends ResponsiveLayout implements HasDynamicTitle {

    public static final String ROUTE = "chat";

    private static final String TOPIC = "chat";

    private final UserInfo currentUser;

    public ChatView(final SecurityService securityService) {
        final var username = securityService.getAuthenticatedUser().getUsername();
        currentUser = new UserInfo(username, username);

        add(createHeader(getTranslation("chat.page.subtitle"), createAudience()), createContent(createChat()), createFooter());
    }

    @Override
    public String getPageTitle() {
        return getTranslation("site.base.title", getTranslation("menu.main.chat"));
    }

    private Component createChat() {
        final var messages = new CollaborationMessageList(currentUser, TOPIC);
        messages.addClassName("chat-messages");
        final var field = new TextField();
        field.addClassName("chat-field");
        field.setMaxLength(255);
        field.setPlaceholder(getTranslation("chat.field.placeholder"));
        final var button = new Button(getTranslation("chat.button.send"));
        button.addClickShortcut(Key.ENTER);
        button.addClassName("chat-button");
        button.setEnabled(false);

        messages.setSubmitter(activationContext -> {
            button.setEnabled(true);
            final var registration = button.addClickListener(event -> {
                final var value = field.getValue();
                if (!value.isEmpty()) {
                    activationContext.appendMessage(value);
                    field.clear();
                }
            });
            return () -> {
                registration.remove();
                button.setEnabled(false);
            };
        });
        final var input = new HorizontalLayout(field, button);
        input.setWidthFull();
        final var layout = new VerticalLayout(messages, input);
        layout.setWidthFull();
        layout.expand(messages);
        messages.setWidthFull();
        return layout;
    }

    private Component createAudience() {
        final var layout = new VerticalLayout();

        final var presenceManager = new PresenceManager(layout, currentUser, TOPIC);
        presenceManager.markAsPresent(true);
        presenceManager.setNewUserHandler(newUserInfo -> {
            final var card = createUserCard(newUserInfo);
            layout.add(card);
            return () -> layout.remove(card);
        });

        return layout;
    }

    private Component createUserCard(final UserInfo user) {
        return new HorizontalLayout(new Avatar(user.getName()));
    }

}
