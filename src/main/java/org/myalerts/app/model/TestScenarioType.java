package org.myalerts.app.model;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.vaadin.flow.data.provider.Query;
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

    public String getLabelAsLowercase() {
        return label.toLowerCase();
    }

    public static Stream<TestScenarioType> findByQuery(Query<TestScenarioType, String> query) {
        return query.getFilter().map(value -> Arrays.stream(values())
            .filter(testScenarioType -> testScenarioType.getLabel().contains(value))
            .skip(query.getOffset())
            .limit(query.getLimit()))
            .orElseGet(() -> Arrays.stream(values()).skip(query.getOffset()).limit(query.getLimit()));
    }

}
