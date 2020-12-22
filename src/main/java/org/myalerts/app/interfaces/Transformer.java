package org.myalerts.app.interfaces;

public interface Transformer<K, T> {

    T transform(K input);

}
