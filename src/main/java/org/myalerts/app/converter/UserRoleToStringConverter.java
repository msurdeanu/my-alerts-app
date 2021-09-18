package org.myalerts.app.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.myalerts.app.model.UserRole;

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
