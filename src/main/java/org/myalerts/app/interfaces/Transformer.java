package org.myalerts.app.interfaces;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface Transformer<K, T> {

    T transform(K input);

}
