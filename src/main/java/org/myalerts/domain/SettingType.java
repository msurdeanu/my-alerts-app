package org.myalerts.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static java.util.Arrays.stream;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RequiredArgsConstructor
public enum SettingType {

    TEXT("text"),
    TEXT_H("text_h"),
    PASSWORD("pass"),
    INTEGER("int"),
    INTEGER_H("int_h"),
    BOOLEAN("bool"),
    BOOLEAN_H("bool_h");

    @Getter
    private final String value;

    public static SettingType of(final String value) {
        return stream(values())
            .filter(settingType -> settingType.getValue().equals(value))
            .findFirst()
            .orElse(null);
    }

}
