package org.myalerts.app.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "logout")
public class LogoutView extends Div implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        SecurityContextHolder.clearContext();
        VaadinService.getCurrentRequest().getWrappedSession().invalidate();
    }
}
