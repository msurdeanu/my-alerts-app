package org.myalerts.app.model;

import java.util.Arrays;

import de.codecamp.vaadin.security.spring.access.VaadinSecurity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@AllArgsConstructor
public enum UserRole {

    GUEST("ROLE_GUEST"),
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    NOT_GUEST("!ROLE_GUEST");

    @Getter
    private final String label;

    public boolean validate() {
        return UserRole.GUEST.equals(this) || UserRole.NOT_GUEST.equals(this) && VaadinSecurity.check().isAuthenticated() ||
            VaadinSecurity.check().hasRole(getLabel());
    }

    public static UserRole of(final String label) {
        return Arrays.stream(UserRole.values())
            .filter(role -> role.getLabel().equals(label))
            .findFirst()
            .orElse(UserRole.GUEST);
    }

}
