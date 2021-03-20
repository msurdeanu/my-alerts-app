package org.myalerts.app.transformer;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.myalerts.app.model.Setting;
import org.myalerts.app.provider.SettingProvider;

import static org.myalerts.app.provider.SettingProvider.BOOLEAN_TYPE;
import static org.myalerts.app.provider.SettingProvider.INTEGER_TYPE;
import static org.myalerts.app.provider.SettingProvider.PASSWORD_TYPE;
import static org.myalerts.app.provider.SettingProvider.STRING_TYPE;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class SettingsToComponentsTransformer implements Transformer<List<Setting>, List<Component>> {

    private final Binder<SettingProvider> binder;

    @Override
    public List<Component> transform(List<Setting> settings) {
        return settings.stream().map(this::createComponent).collect(Collectors.toList());
    }

    private Component createComponent(Setting setting) {
        final String type = setting.getType();
        switch (type) {
            case STRING_TYPE:
                return createTextField(setting);
            case PASSWORD_TYPE:
                return createPasswordField(setting);
            case INTEGER_TYPE:
                return createIntegerField(setting);
            case BOOLEAN_TYPE:
                return createToggleButton(setting);
            default:
                throw new NoSuchElementException("There is no display setting type to met your requirements.");
        }
    }

    private Component createTextField(Setting setting) {
        final TextField textField = new TextField();
        textField.setLabel(textField.getTranslation(setting.getTitle()));
        textField.setHelperText(textField.getTranslation(setting.getDescription()));
        if (!setting.isEditable()) {
            textField.setReadOnly(true);
        }
        binder.forField(textField).bind(
            settingProvider -> settingProvider.getOrDefault(Setting.Key.fromString(setting.getKey()), StringUtils.EMPTY),
            (settingProvider, newValue) -> settingProvider.set(Setting.Key.fromString(setting.getKey()), newValue)
        );
        return textField;
    }

    private PasswordField createPasswordField(Setting setting) {
        final PasswordField passwordField = new PasswordField();
        passwordField.setLabel(passwordField.getTranslation(setting.getTitle()));
        passwordField.setHelperText(passwordField.getTranslation(setting.getDescription()));
        if (!setting.isEditable()) {
            passwordField.setReadOnly(true);
        }
        binder.forField(passwordField).bind(
            settingProvider -> settingProvider.getOrDefault(Setting.Key.fromString(setting.getKey()), StringUtils.EMPTY),
            (settingProvider, newValue) -> settingProvider.set(Setting.Key.fromString(setting.getKey()), newValue)
        );
        return passwordField;
    }

    private IntegerField createIntegerField(Setting setting) {
        IntegerField integerField = new IntegerField();
        integerField.setLabel(integerField.getTranslation(setting.getTitle()));
        integerField.setHelperText(integerField.getTranslation(setting.getDescription()));
        if (!setting.isEditable()) {
            integerField.setReadOnly(true);
        }
        binder.forField(integerField).bind(
            settingProvider -> settingProvider.getOrDefault(Setting.Key.fromString(setting.getKey()), 0),
            (settingProvider, newValue) -> settingProvider.set(Setting.Key.fromString(setting.getKey()), newValue)
        );
        return integerField;
    }

    private ToggleButton createToggleButton(Setting setting) {
        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setLabel(toggleButton.getTranslation(setting.getTitle()));
        if (!setting.isEditable()) {
            toggleButton.setReadOnly(true);
        }
        binder.forField(toggleButton).bind(
            settingProvider -> settingProvider.getOrDefault(Setting.Key.fromString(setting.getKey()), Boolean.FALSE),
            (settingProvider, newValue) -> settingProvider.set(Setting.Key.fromString(setting.getKey()), newValue)
        );
        return toggleButton;
    }

}
