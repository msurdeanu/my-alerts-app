package org.myalerts.app.i18n;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.vaadin.flow.i18n.I18NProvider;
import lombok.extern.slf4j.Slf4j;

import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static java.util.ResourceBundle.getBundle;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
public class CustomI18NProvider implements I18NProvider {

    private static final ResourceBundle RESOURCE_BUNDLE_EN = getBundle("translation", ENGLISH);

    @Override
    public List<Locale> getProvidedLocales() {
        return singletonList(ENGLISH);
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... args) {
        if (RESOURCE_BUNDLE_EN.containsKey(key)) {
            return String.format(RESOURCE_BUNDLE_EN.getString(key), args);
        }

        log.warn("Missing translation for key {}", key);
        return key;
    }

}
