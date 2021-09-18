package org.myalerts.app.service;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

import com.vaadin.flow.server.VaadinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.myalerts.app.model.Setting;
import org.myalerts.app.provider.SettingProvider;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CookieStoreService {

    public static final String THEME_DARK_COOKIE = "themeDark";

    private final SettingProvider settingProvider;

    public boolean isSetAndTrue(final String name) {
        return findByName(name)
            .map(cookie -> Boolean.TRUE.toString().equalsIgnoreCase(cookie.getValue()))
            .orElse(false);
    }

    public void set(final String name, final boolean value) {
        set(name, Boolean.toString(value), settingProvider.getOrDefault(Setting.Key.COOKIE_EXPIRY_IN_SECONDS, (int) TimeUnit.MINUTES.toSeconds(15)));
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
