package org.myalerts.app.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.vaadin.flow.component.icon.VaadinIcon;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Converter
public class VaadinIconToStringConverter implements AttributeConverter<VaadinIcon, String> {

    @Override
    public String convertToDatabaseColumn(VaadinIcon attribute) {
        return ofNullable(attribute).map(Enum::toString).orElse(null);
    }

    @Override
    public VaadinIcon convertToEntityAttribute(String data) {
        return ofNullable(data).map(VaadinIcon::valueOf).orElse(null);
    }

}
