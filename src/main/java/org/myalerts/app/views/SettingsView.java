package org.myalerts.app.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.router.Route;
import org.myalerts.app.interfaces.HasSettings;
import org.myalerts.app.layouts.BaseLayout;
import org.myalerts.app.layouts.ResponsiveLayout;
import org.myalerts.app.transformers.SettingsToFormTransformer;

import java.util.List;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Route(value = SettingsView.ROUTE, layout = BaseLayout.class)
public class SettingsView extends ResponsiveLayout {

    public static final String ROUTE = "settings";

    public SettingsView(List<HasSettings> hasSettingBeans) {
        add(createHeader("Settings"), createContent(createFormLayout(hasSettingBeans)), createFooter());
    }

    private Component createFormLayout(List<HasSettings> hasSettingBeans) {
        FormLayout nameLayout = new FormLayout();
        new SettingsToFormTransformer().transform(hasSettingBeans).forEach(nameLayout::add);

        nameLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("250px", 1),
                new FormLayout.ResponsiveStep("500px", 2),
                new FormLayout.ResponsiveStep("750px", 3),
                new FormLayout.ResponsiveStep("1000px", 4));

        return nameLayout;
    }

}
