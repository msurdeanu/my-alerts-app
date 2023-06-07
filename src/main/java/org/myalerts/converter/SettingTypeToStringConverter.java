package org.myalerts.converter;

import org.myalerts.domain.SettingType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Converter
public class SettingTypeToStringConverter implements AttributeConverter<SettingType, String> {

    @Override
    public String convertToDatabaseColumn(final SettingType attribute) {
        return ofNullable(attribute).map(SettingType::getValue).orElse(null);
    }

    @Override
    public SettingType convertToEntityAttribute(final String data) {
        return ofNullable(data).map(SettingType::of).orElse(null);
    }

}
