package org.myalerts.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import org.myalerts.layout.BaseLayout;
import org.myalerts.layout.ResponsiveLayout;
import org.myalerts.provider.SettingProvider;
import org.myalerts.transformer.SettingsToComponentsTransformer;

import javax.annotation.security.RolesAllowed;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RolesAllowed("ROLE_ADMIN")
@Route(value = SettingView.ROUTE, layout = BaseLayout.class)
public class SettingView extends ResponsiveLayout implements HasDynamicTitle {

    public static final String ROUTE = "settings";

    private final Binder<SettingProvider> binder = new Binder<>();

    private final SettingProvider settingProvider;

    public SettingView(final SettingProvider settingProvider) {
        this.settingProvider = settingProvider;

        add(createHeader(getTranslation("settings.page.subtitle")), createContent(createFormLayout()), createFooter());
    }

    @Override
    public String getPageTitle() {
        return getTranslation("site.base.title", getTranslation("menu.main.settings"));
    }

    private Component createFormLayout() {
        final var layout = new FormLayout();

        new SettingsToComponentsTransformer(binder).transform(settingProvider.getAll()).forEach(layout::add);
        binder.readBean(settingProvider);

        final var saveButton = createSaveButton();
        binder.addStatusChangeListener(event -> saveButton.setEnabled(event.getBinder().hasChanges() && event.getBinder().isValid()));
        layout.add(saveButton);

        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("250px", 1),
                new FormLayout.ResponsiveStep("500px", 2),
                new FormLayout.ResponsiveStep("750px", 3),
                new FormLayout.ResponsiveStep("1000px", 4)
        );

        return layout;
    }

    private Button createSaveButton() {
        final var saveButton = new Button(getTranslation("settings.button.save"), event -> onClickListener());
        saveButton.setEnabled(false);

        return saveButton;
    }

    private void onClickListener() {
        try {
            binder.writeBean(settingProvider);
        } catch (ValidationException notUsed) {
            // Nothing to do
        }
    }

}
