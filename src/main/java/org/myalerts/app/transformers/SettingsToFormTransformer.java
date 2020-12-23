package org.myalerts.app.transformers;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.app.interfaces.DisplaySetting;
import org.myalerts.app.interfaces.HasSettings;
import org.myalerts.app.interfaces.Transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
public class SettingsToFormTransformer implements Transformer<List<HasSettings>, List<Component>> {

    @Override
    public List<Component> transform(List<HasSettings> beans) {
        List<Component> components = new ArrayList<>();

        for (HasSettings bean : beans) {
            final Binder<HasSettings> binder = new Binder<>();
            binder.setBean(bean);

            Arrays.stream(bean.getClass().getSuperclass().getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(DisplaySetting.class))
                    .filter(field -> bean.hasGetter(field.getName(), true))
                    .forEach(field -> components.add(createComponent(field.getName(),
                            field.getAnnotation(DisplaySetting.class), binder)));
        }

        return components;
    }

    private Component createComponent(String fieldName, DisplaySetting displaySetting, Binder<HasSettings> binder) {
        final DisplaySetting.Type type = displaySetting.type();
        switch (type) {
            case TEXT:
                return createTextFieldAndBind(fieldName, displaySetting, binder);
            case PASSWORD:
                return createPasswordFieldAndBind(fieldName, displaySetting, binder);
            case INTEGER:
                return createIntegerFieldAndBind(fieldName, displaySetting, binder);
            default:
                throw new NoSuchElementException("There is no display setting type to meet your requirements.");
        }
    }

    private Component createTextFieldAndBind(String fieldName, DisplaySetting displaySetting, Binder<HasSettings> binder) {
        TextField textField = new TextField(displaySetting.label());
        textField.setLabel(textField.getTranslation(displaySetting.label()));
        textField.setHelperText(textField.getTranslation(displaySetting.helper()));
        if (!binder.getBean().hasSetter(fieldName, true)) {
            textField.setReadOnly(true);
        }

        binder.forField(textField).bind(
                (objectHasSettings) -> (String) objectHasSettings.findAndInvokeGetterFor(fieldName, true),
                (objectHasSettings, newValue) -> objectHasSettings.findAndInvokeSetterFor(fieldName, newValue, true)
        );

        return textField;
    }

    private PasswordField createPasswordFieldAndBind(String fieldName, DisplaySetting displaySetting, Binder<HasSettings> binder) {
        PasswordField passwordField = new PasswordField();
        passwordField.setLabel(passwordField.getTranslation(displaySetting.label()));
        passwordField.setHelperText(passwordField.getTranslation(displaySetting.helper()));
        if (!binder.getBean().hasSetter(fieldName, true)) {
            passwordField.setReadOnly(true);
        }

        binder.forField(passwordField).bind(
                (objectHasSettings) -> (String) objectHasSettings.findAndInvokeGetterFor(fieldName, true),
                (objectHasSettings, newValue) -> objectHasSettings.findAndInvokeSetterFor(fieldName, newValue, true)
        );

        return passwordField;
    }

    private IntegerField createIntegerFieldAndBind(String fieldName, DisplaySetting displaySetting, Binder<HasSettings> binder) {
        IntegerField integerField = new IntegerField();
        integerField.setLabel(integerField.getTranslation(displaySetting.label()));
        integerField.setHelperText(integerField.getTranslation(displaySetting.helper()));
        if (!binder.getBean().hasSetter(fieldName, true)) {
            integerField.setReadOnly(true);
        }

        binder.forField(integerField).bind(
                (objectHasSettings) -> (Integer) objectHasSettings.findAndInvokeGetterFor(fieldName, true),
                (objectHasSettings, newValue) -> objectHasSettings.findAndInvokeSetterFor(fieldName, newValue, true)
        );

        return integerField;
    }

}
