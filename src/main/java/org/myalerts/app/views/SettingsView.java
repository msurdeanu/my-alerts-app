package org.myalerts.app.views;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.myalerts.app.interfaces.RequiresAdminRole;
import org.myalerts.app.layouts.BaseLayout;

@Route(value = SettingsView.ROUTE, layout = BaseLayout.class)
@RequiresAdminRole
public class SettingsView extends FlexLayout {

    public static final String ROUTE = "settings";

    public SettingsView() {
        FormLayout nameLayout = new FormLayout();

        TextField titleField = new TextField();
        titleField.setLabel("Title");
        titleField.setPlaceholder("Sir");
        TextField firstNameField = new TextField();
        firstNameField.setLabel("First name");
        firstNameField.setPlaceholder("John");
        TextField lastNameField = new TextField();
        lastNameField.setLabel("Last name");
        lastNameField.setPlaceholder("Doe");

        nameLayout.add(titleField, firstNameField, lastNameField);

        nameLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));
        add(nameLayout);
    }

}
