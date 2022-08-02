package org.myalerts.provider;

import com.vaadin.flow.i18n.I18NProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.api.provider.TranslationsProvider;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static java.util.Optional.ofNullable;
import static java.util.ResourceBundle.getBundle;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Component
@DependsOn("pluginManager")
@RequiredArgsConstructor
public class CustomI18NProvider implements I18NProvider {

    public static final String PRETTY_TIME_FORMAT = "pretty.time.format";

    private static final ResourceBundle DEFAULT_RESOURCE_BUNDLE = getBundle("translation", ENGLISH);

    private static final Map<String, ResourceBundle> LANGUAGE_RESOURCE_MAP = Map.of("en", DEFAULT_RESOURCE_BUNDLE);

    private final ApplicationContext applicationContext;

    private PrettyTime cachedPrettyTime;

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(ENGLISH);
    }

    @Override
    public String getTranslation(final String key, final Locale locale, final Object... args) {
        if (key == null) {
            return null;
        }

        if (PRETTY_TIME_FORMAT.equals(key) && args.length == 1 && args[0] instanceof Instant) {
            return prettyTimeFormat((Instant) args[0], locale);
        }

        // Try to find key in local resource bundle
        final var resourceBundle = LANGUAGE_RESOURCE_MAP.getOrDefault("en", DEFAULT_RESOURCE_BUNDLE);
        if (resourceBundle.containsKey(key)) {
            return format(resourceBundle.getString(key), args);
        }

        // Try to find key in plugin providers, otherwise return key as it is
        return findTranslationInProviders("en", key)
            .map(translation -> format(translation, args))
            .orElseGet(() -> {
                log.warn("Missing translation for key '{}' and language '{}'.", key, "en");
                return key;
            });
    }

    private Optional<String> findTranslationInProviders(final String language, final String key) {
        return applicationContext.getBeansOfType(TranslationsProvider.class)
            .values()
            .stream()
            .map(provider -> provider.getTranslations(language))
            .map(translations -> translations.get(key))
            .filter(Objects::nonNull)
            .findFirst();
    }

    private String prettyTimeFormat(final Instant time, final Locale locale) {
        cachedPrettyTime = ofNullable(cachedPrettyTime)
            .filter(prettyTime -> prettyTime.getLocale().equals(locale))
            .orElseGet(() -> new PrettyTime(locale));
        return cachedPrettyTime.format(time);
    }

}
