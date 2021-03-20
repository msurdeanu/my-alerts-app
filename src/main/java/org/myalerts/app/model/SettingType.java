package org.myalerts.app.model;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SettingType {

    TEXT("text"),
    PASSWORD("pass"),
    INTEGER("int"),
    BOOLEAN("bool");

    @Getter
    private final String value;

    public static SettingType from(String value) {
        return Arrays.stream(values())
            .filter(settingType -> settingType.getValue().equalsIgnoreCase(value))
            .findFirst()
            .orElse(null);
    }

}
