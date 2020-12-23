package org.myalerts.app.services;

import com.vaadin.flow.server.VaadinService;
import lombok.RequiredArgsConstructor;
import org.myalerts.app.configs.CookieStoreConfig;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CookieStoreService {

    public static final String THEME_DARK_COOKIE = "themeDark";

    private final CookieStoreConfig cookieStoreConfig;

    public boolean isSetAndTrue(String name) {
        return findByName(name)
                .map(cookie -> Boolean.TRUE.toString().equalsIgnoreCase(cookie.getValue()))
                .orElse(false);
    }

    public void set(String name, boolean value) {
        set(name, Boolean.toString(value), cookieStoreConfig.getExpiryInSeconds());
    }

    public void set(String name, String value, int expiryInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(expiryInSeconds);
        VaadinService.getCurrentResponse().addCookie(cookie);
    }

    private Optional<Cookie> findByName(String name) {
        return Arrays.stream(VaadinService.getCurrentRequest().getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst();
    }

}
