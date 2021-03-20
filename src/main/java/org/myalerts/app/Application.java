package org.myalerts.app;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import org.myalerts.app.event.Event;
import org.myalerts.app.event.EventBroadcaster;
import org.myalerts.app.i18n.CustomI18NProvider;

@SpringBootApplication
@RequiredArgsConstructor
public class Application extends SpringBootServletInitializer implements AppShellConfigurator, VaadinServiceInitListener {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent) {
        System.setProperty("vaadin.i18n.provider", CustomI18NProvider.class.getName());
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
