package org.myalerts.app.model;

import java.util.Arrays;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.myalerts.app.converter.SettingTypeToStringConverter;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Entity
@Getter
@Table(name = "settings")
public class Setting {

    @Id
    private String key;

    private String title;

    private String description;

    @Convert(converter = SettingTypeToStringConverter.class)
    private SettingType type;

    @Setter
    private String value;

    private boolean editable;

    private int sequence;

    @Transient
    @Setter
    private Object computedValue;

    @RequiredArgsConstructor
    public enum Key {

        CookieExpiryInSeconds("cookieExpiryInSeconds"),
        TestScenarioPoolSize("testScenarioPoolSize"),
        TestScenarioThreadNamePrefix("testScenarioThreadNamePrefix");

        @Getter
        private final String key;

        public static Key from(String value) {
            return Arrays.stream(Key.values())
                .filter(item -> item.getKey().equals(value))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No key found for given value"));
        }

    }

}
