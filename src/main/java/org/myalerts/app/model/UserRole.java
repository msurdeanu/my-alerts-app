package org.myalerts.app.model;

import java.util.Arrays;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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
        return UserRole.GUEST.equals(this) || UserRole.NOT_GUEST.equals(this) && isAuthenticated() || hasRole(getLabel());
    }

    private boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails;
    }

    private boolean hasRole(final String role) {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(auth -> auth.getAuthorities().stream().anyMatch(granted -> granted.getAuthority().equals(role)))
            .orElse(false);
    }

    public static UserRole of(final String label) {
        return Arrays.stream(UserRole.values())
            .filter(role -> role.getLabel().equals(label))
            .findFirst()
            .orElse(UserRole.GUEST);
    }

}
