package org.myalerts.domain;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@AllArgsConstructor
public enum TestScenarioType {

    DISABLED("disabled", testScenario -> !testScenario.isEnabled()),
    FAILED("failed", TestScenario::isFailed),
    PASSED("passed", testScenario -> !testScenario.isFailed()),
    ALL("all", testScenario -> true);

    @Getter
    private final String label;

    @Getter
    private final Predicate<? super TestScenario> filter;

    public static List<TestScenarioType> getAllItems() {
        return Arrays.stream(values()).collect(Collectors.toList());
    }

}
