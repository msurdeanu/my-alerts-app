package org.myalerts.converter;

import org.myalerts.domain.UserRole;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Converter
public class UserRoleToStringConverter implements AttributeConverter<UserRole, String> {

    @Override
    public String convertToDatabaseColumn(UserRole attribute) {
        return ofNullable(attribute).map(UserRole::getLabel).orElse(null);
    }

    @Override
    public UserRole convertToEntityAttribute(String data) {
        return ofNullable(data).map(UserRole::of).orElse(UserRole.GUEST);
    }

}
