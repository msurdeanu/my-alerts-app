package org.myalerts.app.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.Route;

import org.myalerts.app.layout.BaseLayout;
import org.myalerts.app.layout.ResponsiveLayout;
import org.myalerts.app.model.User;
import org.myalerts.app.service.CustomUserDetailsService;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Route(value = RegisterView.ROUTE, layout = BaseLayout.class)
public class RegisterView extends ResponsiveLayout {

    public static final String ROUTE = "register";

    private static final int MIN_CHARACTERS_FOR_PASSWORDS = 8;

    private final CustomUserDetailsService userDetailsService;

    private TextField usernameField;

    private EmailField emailField;

    private PasswordField passwordField1;

    private PasswordField passwordField2;

    private Span errorMessage;

    private boolean enablePasswordValidation;

    private BeanValidationBinder<User> userBinder = new BeanValidationBinder<>(User.class);

    public RegisterView(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        add(createHeader(getTranslation("register.page.title")), createContent(createFormLayout()), createFooter());

        createBinder();
    }

    private Component createFormLayout() {
        usernameField = new TextField(getTranslation("register.form.field.username"));
        emailField = new EmailField(getTranslation("register.form.field.email"));
        passwordField1 = new PasswordField(getTranslation("register.form.field.password"));
        passwordField2 = new PasswordField(getTranslation("register.form.field.confirm-password"));
        errorMessage = new Span();

        final Button submitButton = new Button(getTranslation("register.form.field.submit"));

        submitButton.addClickListener(event -> {
            User user = new User();

            if (userBinder.writeBeanIfValid(user) && userDetailsService.registerUser(user)) {
                //Notification.show(getTranslation("register.form.successful"));
            } else {
                errorMessage.setText(getTranslation("register.form.not-successful"));
            }
        });

        final FormLayout formLayout = new FormLayout(usernameField, emailField, passwordField1, passwordField2, errorMessage, submitButton);

        formLayout.setMaxWidth("500px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setColspan(errorMessage, 2);
        formLayout.setColspan(submitButton, 2);

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
            new FormLayout.ResponsiveStep("500px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

        return formLayout;
    }

    private void createBinder() {
        userBinder.forField(usernameField).asRequired().bind("username");
        userBinder.forField(emailField).asRequired(new EmailValidator(getTranslation("register.form.invalid-email-address"))).bind("email");
        userBinder.forField(passwordField1).asRequired().withValidator(this::passwordValidator).bind("password");

        passwordField2.addValueChangeListener(event -> {
            enablePasswordValidation = true;

            userBinder.validate();
        });

        userBinder.setStatusLabel(errorMessage);
    }

    private ValidationResult passwordValidator(String password, ValueContext valueContext) {
        if (password == null || password.length() < MIN_CHARACTERS_FOR_PASSWORDS) {
            return ValidationResult.error(getTranslation("register.form.invalid-password-length", MIN_CHARACTERS_FOR_PASSWORDS));
        }

        if (!enablePasswordValidation) {
            enablePasswordValidation = true;
            return ValidationResult.ok();
        }

        final String confirmedPassword = passwordField2.getValue();
        if (password.equals(confirmedPassword)) {
            return ValidationResult.ok();
        }

        return ValidationResult.error(getTranslation("register.form.invalid-password-match"));
    }
}
