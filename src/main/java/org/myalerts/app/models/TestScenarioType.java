package org.myalerts.app.models;

import com.vaadin.flow.data.provider.Query;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Stream;

@AllArgsConstructor
public enum TestScenarioType {

    DISABLED("disabled"),
    FAILED("failed"),
    PASSED("passed"),
    ALL("all");

    @Getter
    private final String label;

    public String getLabelAsLowercase() {
        return label.toLowerCase();
    }

    public static TestScenarioType getEnum(String value) {
        return Arrays.stream(values())
                .filter(testScenarioType -> testScenarioType.getLabel().equalsIgnoreCase(value))
                .findFirst()
                .orElse(TestScenarioType.ALL);
    }

    public static Stream<TestScenarioType> findByQuery(Query<TestScenarioType, String> query) {
        return query.getFilter().map(value -> Arrays.stream(values())
                .filter(testScenarioType -> testScenarioType.getLabel().contains(value))
                .skip(query.getOffset())
                .limit(query.getLimit()))
                .orElseGet(() -> Arrays.stream(values()).skip(query.getOffset()).limit(query.getLimit()));
    }

}
