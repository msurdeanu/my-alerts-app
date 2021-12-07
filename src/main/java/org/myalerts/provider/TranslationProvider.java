package org.myalerts.provider;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.vaadin.flow.i18n.I18NProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.stereotype.Component;

import org.myalerts.model.Setting;

import static java.util.Locale.ENGLISH;
import static java.util.ResourceBundle.getBundle;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TranslationProvider implements I18NProvider {

    private static final PrettyTime PRETTY_TIME = new PrettyTime();

    private static final ResourceBundle DEFAULT_RESOURCE_BUNDLE = getBundle("translation", ENGLISH);

    private static final Map<String, ResourceBundle> LANGUAGE_RESOURCE_MAP = Map.of("en", DEFAULT_RESOURCE_BUNDLE);

    private final SettingProvider settingProvider;

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(ENGLISH);
    }

    @Override
    public String getTranslation(final String key, final Locale locale, final Object... args) {
        if (key == null) {
            return null;
        }

        final String language = settingProvider.getOrDefault(Setting.Key.LANGUAGE, "en");
        final ResourceBundle resourceBundle = LANGUAGE_RESOURCE_MAP.getOrDefault(language, DEFAULT_RESOURCE_BUNDLE);
        if (resourceBundle.containsKey(key)) {
            return String.format(resourceBundle.getString(key), args);
        }

        log.warn("Missing translation for key '{}' and language '{}'.", key, language);
        return key;
    }

    public String prettyTimeFormat(final Instant time) {
        return PRETTY_TIME.format(time);
    }

}
