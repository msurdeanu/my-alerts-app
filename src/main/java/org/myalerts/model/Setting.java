package org.myalerts.model;

import java.util.Arrays;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.myalerts.converter.SettingTypeToStringConverter;
import org.myalerts.exception.AlertingException;

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

    private int position;

    @Transient
    @Setter
    private Object computedValue;

    @RequiredArgsConstructor
    public enum Key {

        LANGUAGE("language"),
        CACHE_MENU_ITEM_MAX_SIZE("cacheMenuItemMaxSize"),
        CACHE_MENU_ITEM_EXPIRE_AFTER_ACCESS("cacheMenuItemExpireAfterAccess"),
        CACHE_MENU_ITEM_EXPIRE_AFTER_WRITE("cacheMenuItemExpireAfterWrite"),
        CACHE_TEST_SCENARIO_RESULT_MAX_SIZE("cacheTestScenarioResultMaxSize"),
        CACHE_TEST_SCENARIO_RESULT_EXPIRE_AFTER_ACCESS("cacheTestScenarioResultExpireAfterAccess"),
        CACHE_TEST_SCENARIO_RESULT_EXPIRE_AFTER_WRITE("cacheTestScenarioResultExpireAfterWrite"),
        COOKIE_EXPIRY_IN_SECONDS("cookieExpiryInSeconds"),
        GRID_PAGE_SIZE("gridPageSize"),
        GRID_PAGINATOR_SIZE("gridPaginatorSize"),
        TEST_SCENARIO_POOL_SIZE("testScenarioPoolSize"),
        TEST_SCENARIO_THREAD_NAME_PREFIX("testScenarioThreadNamePrefix"),
        TEST_SCENARIO_EXEC_TIMEOUT("testScenarioExecTimeout");

        @Getter
        private final String key;

        public static Key of(String value) {
            return Arrays.stream(Key.values())
                .filter(item -> item.getKey().equals(value))
                .findFirst()
                .orElseThrow(() -> new AlertingException("No key found for '" + value + "'"));
        }

    }

}
