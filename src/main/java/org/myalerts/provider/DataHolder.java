package org.myalerts.provider;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RequiredArgsConstructor
public final class DataHolder<T> {

    private final AtomicBoolean dirtyFlag = new AtomicBoolean(true);

    private final Supplier<T> dataSupplier;

    private T data;

    public T get() {
        if (dirtyFlag.compareAndSet(true, false)) {
            data = dataSupplier.get();
        }

        return data;
    }

    public void invalidate() {
        dirtyFlag.set(true);
    }

}
