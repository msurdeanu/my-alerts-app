package org.myalerts.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.myalerts.model.UserRole;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Converter
public class UserRoleToStringConverter implements AttributeConverter<UserRole, String> {

    @Override
    public String convertToDatabaseColumn(final UserRole attribute) {
        return ofNullable(attribute).map(UserRole::getLabel).orElse(null);
    }

    @Override
    public UserRole convertToEntityAttribute(final String data) {
        return ofNullable(data).map(UserRole::of).orElse(UserRole.GUEST);
    }

}
