package org.myalerts.app.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.codecamp.vaadin.security.spring.authentication.VaadinAuthenticationService;
import org.myalerts.app.layouts.BaseLayout;

@Route(value = LoginView.ROUTE, layout = BaseLayout.class)
public class LoginView extends Composite<VerticalLayout> {

    public static final String ROUTE = "login";

    @Override
    protected VerticalLayout initContent() {
        VerticalLayout layout = super.initContent();

        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        LoginForm loginForm = new LoginForm();
        loginForm.setForgotPasswordButtonVisible(false);
        layout.add(loginForm);
        Checkbox rememberMe = new Checkbox("Remember me");
        layout.add(rememberMe);
        loginForm.addLoginListener(event -> VaadinAuthenticationService.get().login(this, event.getUsername(), event.getPassword(), rememberMe.isEnabled(),
                result -> {
                    loginForm.setEnabled(true);
                    loginForm.setError(result.isFailure());
                    return false;
                }));

        return layout;
    }

}

