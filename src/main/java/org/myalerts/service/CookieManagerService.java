package org.myalerts.service;

import com.vaadin.flow.server.VaadinService;
import lombok.RequiredArgsConstructor;
import org.myalerts.provider.SettingProvider;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CookieManagerService {

    public static final String THEME_DARK_COOKIE = "themeDark";

    private final SettingProvider settingProvider;

    public boolean isSetAndTrue(final String name) {
        return findByName(name)
            .map(cookie -> Boolean.TRUE.toString().equalsIgnoreCase(cookie.getValue()))
            .orElse(false);
    }

    public void set(final String name, final boolean value) {
        set(name, Boolean.toString(value), settingProvider.getOrDefault("cookieExpiryInSeconds", (int) TimeUnit.MINUTES.toSeconds(15)));
    }

    public void set(final String name, final String value, final int expiryInSeconds) {
        final var cookie = new Cookie(name, value);
        cookie.setMaxAge(expiryInSeconds);
        VaadinService.getCurrentResponse().addCookie(cookie);
    }

    private Optional<Cookie> findByName(final String name) {
        return Arrays.stream(VaadinService.getCurrentRequest().getCookies())
            .filter(cookie -> cookie.getName().equals(name))
            .findFirst();
    }

}
