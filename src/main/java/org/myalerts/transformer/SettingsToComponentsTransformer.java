package org.myalerts.transformer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.myalerts.domain.Setting;
import org.myalerts.domain.SettingType;
import org.myalerts.mapper.Mapper1;
import org.myalerts.provider.SettingProvider;

import java.util.EnumMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class SettingsToComponentsTransformer implements Transformer<List<Setting>, List<Component>> {

    private final Mapper1<SettingType, Setting, Optional<Component>> settingToComponentMapping = createMapper();

    private final Binder<SettingProvider> binder;

    @Override
    public List<Component> transform(List<Setting> settings) {
        return settings.stream().flatMap(setting -> createComponent(setting).stream()).collect(Collectors.toList());
    }

    private Optional<Component> createComponent(Setting setting) {
        return settingToComponentMapping.map(setting.getType(), setting);
    }

    private Mapper1<SettingType, Setting, Optional<Component>> createMapper() {
        return Mapper1.<SettingType, Setting, Optional<Component>>builder(new EnumMap<>(SettingType.class))
            .map(SettingType.STR, setting -> Optional.of(createTextField(setting)))
            .map(SettingType.STR_H, setting -> Optional.empty())
            .map(SettingType.TEXT, setting -> Optional.of(createTextArea(setting)))
            .map(SettingType.TEXT_H, setting -> Optional.empty())
            .map(SettingType.PASSWORD, setting -> Optional.of(createPasswordField(setting)))
            .map(SettingType.INTEGER, setting -> Optional.of(createIntegerField(setting)))
            .map(SettingType.INTEGER_H, setting -> Optional.empty())
            .map(SettingType.BOOLEAN, setting -> Optional.of(createToggleButton(setting)))
            .map(SettingType.BOOLEAN_H, setting -> Optional.empty())
            .unmapped(setting -> {
                throw new NoSuchElementException("Unsupported type given as input for setting key: " + setting.getKey());
            }).build();
    }

    private Component createTextField(Setting setting) {
        final var textField = new TextField();
        textField.setLabel(textField.getTranslation(setting.getTitle()));
        textField.setHelperText(textField.getTranslation(setting.getDescription()));
        if (!setting.isEditable()) {
            textField.setReadOnly(true);
        }

        binder.forField(textField).bind(
            settingProvider -> settingProvider.getOrDefault(setting.getKey(), StringUtils.EMPTY),
            (settingProvider, newValue) -> settingProvider.set(setting.getKey(), newValue)
        );

        return textField;
    }

    private Component createTextArea(Setting setting) {
        final var textArea = new TextArea();
        textArea.setLabel(textArea.getTranslation(setting.getTitle()));
        textArea.setHelperText(textArea.getTranslation(setting.getDescription()));
        if (!setting.isEditable()) {
            textArea.setReadOnly(true);
        }

        binder.forField(textArea).bind(
            settingProvider -> settingProvider.getOrDefault(setting.getKey(), StringUtils.EMPTY),
            (settingProvider, newValue) -> settingProvider.set(setting.getKey(), newValue)
        );

        return textArea;
    }

    private PasswordField createPasswordField(Setting setting) {
        final var passwordField = new PasswordField();
        passwordField.setLabel(passwordField.getTranslation(setting.getTitle()));
        passwordField.setHelperText(passwordField.getTranslation(setting.getDescription()));
        if (!setting.isEditable()) {
            passwordField.setReadOnly(true);
        }

        binder.forField(passwordField).bind(
            settingProvider -> settingProvider.getOrDefault(setting.getKey(), StringUtils.EMPTY),
            (settingProvider, newValue) -> settingProvider.set(setting.getKey(), newValue)
        );

        return passwordField;
    }

    private IntegerField createIntegerField(Setting setting) {
        final var integerField = new IntegerField();
        integerField.setLabel(integerField.getTranslation(setting.getTitle()));
        integerField.setHelperText(integerField.getTranslation(setting.getDescription()));
        if (!setting.isEditable()) {
            integerField.setReadOnly(true);
        }

        binder.forField(integerField).bind(
            settingProvider -> settingProvider.getOrDefault(setting.getKey(), 0),
            (settingProvider, newValue) -> settingProvider.set(setting.getKey(), newValue)
        );

        return integerField;
    }

    private Checkbox createToggleButton(Setting setting) {
        final var toggleButton = new Checkbox();
        toggleButton.setLabel(toggleButton.getTranslation(setting.getTitle()));
        if (!setting.isEditable()) {
            toggleButton.setReadOnly(true);
        }

        binder.forField(toggleButton).bind(
            settingProvider -> settingProvider.getOrDefault(setting.getKey(), Boolean.FALSE),
            (settingProvider, newValue) -> settingProvider.set(setting.getKey(), newValue)
        );

        return toggleButton;
    }

}
