package org.myalerts.app.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.vaadin.flow.component.Component;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Converter
public class ClassOfComponentToStringConverter implements AttributeConverter<Class<? extends Component>, String> {

    @Override
    public String convertToDatabaseColumn(Class<? extends Component> attribute) {
        return ofNullable(attribute).map(Class::getName).orElse(null);
    }

    @Override
    public Class<? extends Component> convertToEntityAttribute(String target) {
        return ofNullable(target)
            .map(this::findComponentClass)
            .orElse(null);
    }

    private Class<? extends Component> findComponentClass(String target) {
        try {
            final Class<?> targetClass = Class.forName(target);
            return Component.class.isAssignableFrom(targetClass) ? (Class<? extends Component>) targetClass : null;
        } catch (ClassNotFoundException notUsed) {
            // If class is not found, the target component will be null and will not appear in the final menu
        }

        return null;
    }

}