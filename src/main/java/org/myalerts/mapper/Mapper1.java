package org.myalerts.mapper;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.jooq.lambda.function.Function1;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class Mapper1<T, I1, R> {

    private final Map<T, Function1<I1, R>> mapping;

    private final Function1<I1, R> unmapped;

    public static <T, I1, R> Builder<T, I1, R> builder(Map<T, Function1<I1, R>> mapping) {
        return new Builder<>(mapping);
    }

    public R map(T type, I1 input) {
        return mapping.getOrDefault(type, unmapped).apply(input);
    }

    @RequiredArgsConstructor
    public static class Builder<T, I1, R> {

        private final Map<T, Function1<I1, R>> mapping;

        private Function1<I1, R> unmapped = i1 -> null;

        public Mapper1<T, I1, R> build() {
            return new Mapper1<>(mapping, unmapped);
        }

        public Mapper1.Builder<T, I1, R> map(T type, Function1<I1, R> function) {
            mapping.put(type, function);
            return this;
        }

        public Mapper1.Builder<T, I1, R> unmapped(Function1<I1, R> unmapped) {
            this.unmapped = unmapped;
            return this;
        }

    }
}
