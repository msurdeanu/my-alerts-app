package org.myalerts.app.model;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RequiredArgsConstructor
public enum SettingType {

    TEXT("text"),
    PASSWORD("pass"),
    INTEGER("int"),
    BOOLEAN("bool");

    @Getter
    private final String value;

    public static SettingType of(final String value) {
        return Arrays.stream(values())
            .filter(settingType -> settingType.getValue().equalsIgnoreCase(value))
            .findFirst()
            .orElse(null);
    }

}
