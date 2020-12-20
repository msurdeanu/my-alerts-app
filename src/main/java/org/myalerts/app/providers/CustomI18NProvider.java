package org.myalerts.app.providers;

import com.vaadin.flow.i18n.I18NProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static java.util.ResourceBundle.getBundle;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
public class CustomI18NProvider implements I18NProvider {

    private static final String RESOURCE_BUNDLE_NAME = "translation";

    private static final ResourceBundle RESOURCE_BUNDLE_EN = getBundle(RESOURCE_BUNDLE_NAME, ENGLISH);

    @Override
    public List<Locale> getProvidedLocales() {
        return singletonList(ENGLISH);
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... objects) {
        if (RESOURCE_BUNDLE_EN.containsKey(key)) {
            return RESOURCE_BUNDLE_EN.getString(key);
        }

        log.warn("Missing translation for key {}", key);
        return key;
    }

}
