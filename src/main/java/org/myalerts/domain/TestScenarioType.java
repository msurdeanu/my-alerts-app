package org.myalerts.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

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

    private static final Map<String, TestScenarioType> ALL_TYPES = Map.of(
            "disabled", TestScenarioType.DISABLED,
            "failed", TestScenarioType.FAILED,
            "passed", TestScenarioType.PASSED,
            "all", TestScenarioType.ALL
    );

    @Getter
    private final String label;

    @Getter
    private final Predicate<? super TestScenario> filter;

    public static List<TestScenarioType> getAllItems() {
        return Arrays.stream(values()).collect(Collectors.toList());
    }

    public static TestScenarioType of(final String label) {
        return ALL_TYPES.getOrDefault(ofNullable(label).orElse(TestScenarioType.ALL.getLabel()), TestScenarioType.ALL);
    }

}
