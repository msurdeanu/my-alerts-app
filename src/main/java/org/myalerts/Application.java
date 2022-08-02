package org.myalerts;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import lombok.RequiredArgsConstructor;
import org.myalerts.api.event.Event;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@SpringBootApplication
@RequiredArgsConstructor
@Theme(value = "simple")
@NpmPackage(value = "lumo-css-framework", version = "^4.0.10")
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    private final ApplicationContext applicationContext;

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener
    @Order(1)
    public void registerAllListeners(final ApplicationReadyEvent event) {
        event.getApplicationContext()
            .getBeansOfType(org.myalerts.api.event.EventListener.class)
            .forEach(this::registerListener);
    }

    private void registerListener(final String key, final org.myalerts.api.event.EventListener<Event> value) {
        applicationContext.getEventBroadcaster().register(value::onEventReceived, value.getEventType());
    }

}
