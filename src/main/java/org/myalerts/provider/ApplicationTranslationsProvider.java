package org.myalerts.provider;

import org.myalerts.domain.SupportedLanguage;
import org.pf4j.Extension;

import java.util.Map;
import java.util.ResourceBundle;

import static java.util.Locale.ENGLISH;
import static java.util.ResourceBundle.getBundle;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Extension(ordinal = -1)
public class ApplicationTranslationsProvider implements TranslationsProvider {

    @Override
    public Map<SupportedLanguage, ResourceBundle> getResourceBundles() {
        return Map.of(SupportedLanguage.ENGLISH, getBundle("translation", ENGLISH));
    }

}
