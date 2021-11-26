package org.myalerts.converter;

import org.junit.jupiter.api.Test;

import org.myalerts.model.UserRole;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public class UserRoleToStringConverterTest {

    @Test
    public void testConvertToDatabaseColumn() {
        final var userRoleToStringConverter = new UserRoleToStringConverter();

        assertNull(userRoleToStringConverter.convertToDatabaseColumn(null));
        assertEquals("ROLE_ADMIN", userRoleToStringConverter.convertToDatabaseColumn(UserRole.ADMIN));
        assertEquals("ROLE_GUEST", userRoleToStringConverter.convertToDatabaseColumn(UserRole.GUEST));
    }

    @Test
    public void testConvertToEntityAttribute() {
        final var userRoleToStringConverter = new UserRoleToStringConverter();

        assertNull(userRoleToStringConverter.convertToEntityAttribute(null));
        assertEquals(UserRole.LOGGED, userRoleToStringConverter.convertToEntityAttribute("ROLE_LOGGED"));
        assertEquals(UserRole.GUEST, userRoleToStringConverter.convertToEntityAttribute("Role_Logged"));
    }

}
