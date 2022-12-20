package org.myalerts.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.myalerts.exception.AlertingRuntimeException;

import java.util.Arrays;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RequiredArgsConstructor
public enum SettingKeyEnum {

    LANGUAGE("language"),
    SALT("salt"),
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
    TEST_SCENARIO_EXEC_TIMEOUT("testScenarioExecTimeout"),
    EVENT_CORE_POOL_SIZE("eventCorePoolSize"),
    EVENT_MAX_POOL_SIZE("eventMaxPoolSize");

    @Getter
    private final String key;

    public static SettingKeyEnum of(final String value) {
        return Arrays.stream(SettingKeyEnum.values())
            .filter(item -> item.getKey().equals(value))
            .findFirst()
            .orElseThrow(() -> new AlertingRuntimeException("No key found for '" + value + "'"));
    }

}
