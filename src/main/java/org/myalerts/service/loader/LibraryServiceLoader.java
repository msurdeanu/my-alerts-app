package org.myalerts.service.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.config.LibraryConfig;
import org.myalerts.holder.ParentClassLoaderHolder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Optional.ofNullable;
import static org.myalerts.config.LibraryConfig.JAR_EXTENSION;
import static org.myalerts.exception.AlertingRuntimeException.wrap;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public final class LibraryServiceLoader implements ServiceLoader {

    private final LibraryConfig libraryConfig;

    private URLClassLoader urlClassLoader;

    @Order(5)
    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void load() {
        final var jarUrls = Arrays.stream(Objects.requireNonNull(new File(libraryConfig.getBaseDirectory())
                .listFiles((dir, name) -> name.endsWith(JAR_EXTENSION))))
            .map(file -> wrap(() -> {
                final var url = file.toURI().toURL();
                log.info("JAR file ready to be loaded: {}", file.getAbsolutePath());
                return url;
            }))
            .toArray(URL[]::new);

        urlClassLoader = new URLClassLoader(jarUrls, getClass().getClassLoader());
        ParentClassLoaderHolder.INSTANCE.setClassLoader(urlClassLoader);
    }

    @Override
    public void unload() {
        ofNullable(urlClassLoader).ifPresent(item -> wrap(() -> {
            item.close();
            ParentClassLoaderHolder.INSTANCE.resetClassLoaderToDefault();
            return null;
        }));
    }

}
