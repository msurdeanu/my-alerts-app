package org.myalerts.transformer;

import java.util.EnumMap;
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

import org.myalerts.mapper.Mapper1;
import org.myalerts.model.Setting;
import org.myalerts.model.SettingType;
import org.myalerts.provider.SettingProvider;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class SettingsToComponentsTransformer implements Transformer<List<Setting>, List<Component>> {

    private final Mapper1<SettingType, Setting, Component> settingToComponentMapping = createMapper();

    private final Binder<SettingProvider> binder;

    @Override
    public List<Component> transform(List<Setting> settings) {
        return settings.stream().map(this::createComponent).collect(Collectors.toList());
    }

    private Component createComponent(Setting setting) {
        return settingToComponentMapping.map(setting.getType(), setting);
    }

    private Mapper1<SettingType, Setting, Component> createMapper() {
        return Mapper1.<SettingType, Setting, Component>builder(new EnumMap<>(SettingType.class))
            .map(SettingType.TEXT, this::createTextField)
            .map(SettingType.PASSWORD, this::createPasswordField)
            .map(SettingType.INTEGER, this::createIntegerField)
            .map(SettingType.BOOLEAN, this::createToggleButton)
            .unmapped(setting -> {
                throw new NoSuchElementException("Unsupported type given as input for setting key: " + setting.getKey());
            }).build();
    }

    private Component createTextField(final Setting setting) {
        final var textField = new TextField();
        textField.setLabel(textField.getTranslation(setting.getTitle()));
        textField.setHelperText(textField.getTranslation(setting.getDescription()));
        if (!setting.isEditable()) {
            textField.setReadOnly(true);
        }

        binder.forField(textField).bind(
            settingProvider -> settingProvider.getOrDefault(Setting.Key.of(setting.getKey()), StringUtils.EMPTY),
            (settingProvider, newValue) -> settingProvider.set(Setting.Key.of(setting.getKey()), newValue)
        );

        return textField;
    }

    private PasswordField createPasswordField(final Setting setting) {
        final var passwordField = new PasswordField();
        passwordField.setLabel(passwordField.getTranslation(setting.getTitle()));
        passwordField.setHelperText(passwordField.getTranslation(setting.getDescription()));
        if (!setting.isEditable()) {
            passwordField.setReadOnly(true);
        }

        binder.forField(passwordField).bind(
            settingProvider -> settingProvider.getOrDefault(Setting.Key.of(setting.getKey()), StringUtils.EMPTY),
            (settingProvider, newValue) -> settingProvider.set(Setting.Key.of(setting.getKey()), newValue)
        );

        return passwordField;
    }

    private IntegerField createIntegerField(final Setting setting) {
        final var integerField = new IntegerField();
        integerField.setLabel(integerField.getTranslation(setting.getTitle()));
        integerField.setHelperText(integerField.getTranslation(setting.getDescription()));
        if (!setting.isEditable()) {
            integerField.setReadOnly(true);
        }

        binder.forField(integerField).bind(
            settingProvider -> settingProvider.getOrDefault(Setting.Key.of(setting.getKey()), 0),
            (settingProvider, newValue) -> settingProvider.set(Setting.Key.of(setting.getKey()), newValue)
        );

        return integerField;
    }

    private ToggleButton createToggleButton(final Setting setting) {
        final var toggleButton = new ToggleButton();
        toggleButton.setLabel(toggleButton.getTranslation(setting.getTitle()));
        if (!setting.isEditable()) {
            toggleButton.setReadOnly(true);
        }

        binder.forField(toggleButton).bind(
            settingProvider -> settingProvider.getOrDefault(Setting.Key.of(setting.getKey()), Boolean.FALSE),
            (settingProvider, newValue) -> settingProvider.set(Setting.Key.of(setting.getKey()), newValue)
        );

        return toggleButton;
    }

}
