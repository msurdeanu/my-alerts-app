package org.myalerts.app.view;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.codecamp.vaadin.security.spring.authentication.VaadinAuthenticationService;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Route(value = LoginView.ROUTE)
public class LoginView extends Composite<VerticalLayout> {

    public static final String ROUTE = "login";

    @Override
    protected VerticalLayout initContent() {
        final var layout = super.initContent();

        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        final var loginForm = new LoginForm();
        loginForm.setForgotPasswordButtonVisible(false);
        layout.add(loginForm);
        loginForm.addLoginListener(event -> VaadinAuthenticationService.get().login(this, event.getUsername(), event.getPassword(), true,
            result -> {
                loginForm.setEnabled(true);
                loginForm.setError(result.isFailure());
                return false;
            }));

        return layout;
    }

}

