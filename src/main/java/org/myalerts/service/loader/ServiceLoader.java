package org.myalerts.service.loader;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface ServiceLoader {

    void load();

    default void unload() {
        // Nothing to do by default
    }

}
