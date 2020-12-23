package org.myalerts.app.interfaces;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface HasSettings {

    default Object findAndInvokeGetterFor(String fieldName, boolean isInvokedOnSuperClass) {
        try {
            return findGetter(fieldName, isInvokedOnSuperClass)
                    .orElseThrow(() -> new NoSuchElementException("No getter found for field " + fieldName))
                    .invoke(this);
        } catch (Exception e) {
            throw new NoSuchElementException("No getter found for field " + fieldName);
        }
    }

    default void findAndInvokeSetterFor(String fieldName, Object newValue, boolean isInvokedOnSuperClass) {
        findSetter(fieldName, isInvokedOnSuperClass).ifPresentOrElse(
                setterMethod -> {
                    try {
                        setterMethod.invoke(this, newValue);
                    } catch (Exception e) {
                        throw new NoSuchElementException("No setter found for field " + fieldName);
                    }
                },
                () -> {
                    throw new NoSuchElementException("No setter found for field " + fieldName);
                }
        );
    }

    default boolean hasGetter(String fieldName, boolean isInvokedOnSuperClass) {
        return findGetter(fieldName, isInvokedOnSuperClass).isPresent();
    }

    default boolean hasSetter(String fieldName, boolean isInvokedOnSuperClass) {
        return findSetter(fieldName, isInvokedOnSuperClass).isPresent();
    }

    private Class<?> getInvocationClass(boolean isInvokedOnSuperClass) {
        return isInvokedOnSuperClass ? getClass().getSuperclass() : getClass();
    }

    private Optional<Method> findGetter(String fieldName, boolean isInvokedOnSuperClass) {
        return Arrays.stream(getInvocationClass(isInvokedOnSuperClass).getDeclaredMethods())
                .filter(this::isGetter)
                .filter(method -> method.getName().endsWith(StringUtils.capitalize(fieldName)))
                .findFirst();
    }

    private Optional<Method> findSetter(String fieldName, boolean isInvokedOnSuperClass) {
        return Arrays.stream(getInvocationClass(isInvokedOnSuperClass).getDeclaredMethods())
                .filter(this::isSetter)
                .filter(method -> method.getName().endsWith(StringUtils.capitalize(fieldName)))
                .findFirst();
    }

    private boolean isGetter(Method method) {
        return ((method.getName().startsWith("get") || method.getName().startsWith("is"))
                && method.getParameterCount() == 0
                && !method.getReturnType().equals(void.class));
    }

    private boolean isSetter(Method method) {
        return (method.getName().startsWith("set")
                && method.getParameterCount() == 1
                && method.getReturnType().equals(void.class));
    }

}
