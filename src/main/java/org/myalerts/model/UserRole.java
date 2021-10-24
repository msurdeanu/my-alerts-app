package org.myalerts.model;

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
    LOGGED("ROLE_LOGGED"),
    NOT_LOGGED("ROLE_NOT_LOGGED"),
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    @Getter
    private final String label;

    public boolean validate() {
        return UserRole.GUEST.equals(this) || UserRole.LOGGED.equals(this) && isAuthenticated()
            || UserRole.NOT_LOGGED.equals(this) && !isAuthenticated()
            || hasRole(getLabel());
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
