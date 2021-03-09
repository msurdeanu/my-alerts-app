package org.myalerts.app.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import org.myalerts.app.interfaces.RequiresAdminRole;
import org.myalerts.app.layout.BaseLayout;
import org.myalerts.app.layout.ResponsiveLayout;
import org.myalerts.app.provider.SettingProvider;
import org.myalerts.app.transformer.SettingsToComponentsTransformer;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RequiresAdminRole
@Route(value = SettingsView.ROUTE, layout = BaseLayout.class)
public class SettingsView extends ResponsiveLayout {

    public static final String ROUTE = "settings";

    private final Binder<SettingProvider> binder = new Binder<>();

    private final SettingProvider settingProvider;

    public SettingsView(SettingProvider settingProvider) {
        this.settingProvider = settingProvider;

        add(createHeader(getTranslation("settings.page.title")), createContent(createFormLayout()), createFooter());
    }

    private Component createFormLayout() {
        FormLayout layout = new FormLayout();

        new SettingsToComponentsTransformer(binder).transform(settingProvider.getAll()).forEach(layout::add);
        binder.readBean(settingProvider);

        final Button saveButton = createSaveButton();
        binder.addStatusChangeListener(event -> saveButton.setEnabled(event.getBinder().hasChanges() && event.getBinder().isValid()));
        layout.add(saveButton);

        layout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("250px", 1),
            new FormLayout.ResponsiveStep("500px", 2),
            new FormLayout.ResponsiveStep("750px", 3),
            new FormLayout.ResponsiveStep("1000px", 4));

        return layout;
    }

    private Button createSaveButton() {
        final Button saveButton = new Button(getTranslation("settings.button.save"), event -> {
            try {
                binder.writeBean(settingProvider);
            } catch (ValidationException e) {
                // Nothing to do
            }
        });
        saveButton.setEnabled(false);

        return saveButton;
    }

}
