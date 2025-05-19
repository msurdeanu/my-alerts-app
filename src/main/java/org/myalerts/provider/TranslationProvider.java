package org.myalerts.provider;

import com.vaadin.flow.i18n.I18NProvider;
import groovy.lang.Tuple2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.ApplicationManager;
import org.myalerts.domain.SupportedLanguage;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Component
@DependsOn("pluginManager")
@RequiredArgsConstructor
public class TranslationProvider implements I18NProvider {

    public static final String PRETTY_TIME_FORMAT = "pretty.time.format";

    private final ApplicationManager applicationManager;

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(ENGLISH);
    }

    @Cacheable(cacheNames = "translation-keys", cacheManager = "translationKeyCacheManager",
            key = "#key", condition = "!'" + PRETTY_TIME_FORMAT + "'.equals(#key)")
    @Override
    public String getTranslation(String key, Locale locale, Object... args) {
        if (key == null) {
            return null;
        }

        if (PRETTY_TIME_FORMAT.equals(key) && args.length == 1 && args[0] instanceof Instant instant) {
            final var prettiedTime = prettyTime(instant);
            return getTranslation(prettiedTime.getV1(), locale, prettiedTime.getV2());
        }

        return applicationManager.getBeansOfTypeAsStream(TranslationsProvider.class)
            .map(TranslationsProvider::getResourceBundles)
            .filter(Objects::nonNull)
            .map(resourcesBundles -> resourcesBundles.get(SupportedLanguage.ENGLISH))
            .filter(Objects::nonNull)
            .filter(resourcesBundle -> resourcesBundle.containsKey(key))
            .map(resourcesBundle -> resourcesBundle.getString(key))
            .map(value -> format(value, args))
            .findFirst()
            .orElse(key);
    }

    private Tuple2<String, Long> prettyTime(Instant instant) {
        final var duration = Duration.between(instant, Instant.now());
        final var seconds = duration.getSeconds();
        if (seconds < 60) {
            return Tuple2.tuple("pretty.time.seconds", seconds);
        } else if (seconds < 3600) {
            return Tuple2.tuple("pretty.time.minutes", seconds / 60);
        } else if (seconds < 86400) {
            return Tuple2.tuple("pretty.time.hours", seconds / 3600);
        } else {
            return Tuple2.tuple("pretty.time.days", seconds / 86400);
        }
    }

}
