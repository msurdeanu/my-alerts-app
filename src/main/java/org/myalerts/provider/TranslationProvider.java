package org.myalerts.provider;

import com.vaadin.flow.i18n.I18NProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.ApplicationManager;
import org.myalerts.domain.SupportedLanguage;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static java.util.Optional.ofNullable;

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

    private PrettyTime cachedPrettyTime;

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(ENGLISH);
    }

    @Cacheable(cacheNames = "translation-keys", cacheManager = "translationKeyCacheManager",
            key = "#key", condition = "!'" + PRETTY_TIME_FORMAT + "'.equals(#key)")
    @Override
    public String getTranslation(final String key, final Locale locale, final Object... args) {
        if (key == null) {
            return null;
        }

        if (PRETTY_TIME_FORMAT.equals(key) && args.length == 1 && args[0] instanceof Instant) {
            return prettyTimeFormat((Instant) args[0], locale);
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

    private String prettyTimeFormat(final Instant time, final Locale locale) {
        cachedPrettyTime = ofNullable(cachedPrettyTime)
            .filter(prettyTime -> prettyTime.getLocale().equals(locale))
            .orElseGet(() -> new PrettyTime(locale));
        return cachedPrettyTime.format(time);
    }

}
