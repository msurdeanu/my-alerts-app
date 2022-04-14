package org.myalerts.converter;

import com.vaadin.flow.component.Component;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Converter
public class ClassOfComponentToStringConverter implements AttributeConverter<Class<? extends Component>, String> {

    @Override
    public String convertToDatabaseColumn(final Class<? extends Component> attribute) {
        return ofNullable(attribute).map(Class::getName).orElse(null);
    }

    @Override
    public Class<? extends Component> convertToEntityAttribute(final String target) {
        return ofNullable(target)
            .map(this::findComponentClass)
            .orElse(null);
    }

    private Class<? extends Component> findComponentClass(final String target) {
        try {
            final Class<?> targetClass = Class.forName(target);
            return Component.class.isAssignableFrom(targetClass) ? (Class<? extends Component>) targetClass : null;
        } catch (ClassNotFoundException notUsed) {
            // If class is not found, the target component will be null and will not appear in the final menu.
            // Enable logging on debug to investigate possible issues.
            log.debug("Component class could not be found for target '{}'", target);
        }

        return null;
    }

}