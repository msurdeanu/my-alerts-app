package org.myalerts.app;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.theme.Theme;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import org.myalerts.app.event.Event;
import org.myalerts.app.event.EventBroadcaster;

@SpringBootApplication
@RequiredArgsConstructor
@Theme(value = "simple")
@NpmPackage(value = "lumo-css-framework", version = "^4.0.10")
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        SpringApplication.run(Application.class, args);
    }

    @EventListener
    @Order(1)
    public void registerAllListeners(ApplicationReadyEvent event) {
        event.getApplicationContext()
            .getBeansOfType(org.myalerts.app.event.EventListener.class)
            .forEach(Application::registerListener);
    }

    private static void registerListener(String key, org.myalerts.app.event.EventListener<Event> value) {
        EventBroadcaster.register(value::onEventReceived, value.getEventType());
    }

}
