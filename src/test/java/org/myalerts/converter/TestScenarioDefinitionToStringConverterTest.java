package org.myalerts.converter;

import org.junit.jupiter.api.Test;
import org.myalerts.model.TestScenarioDefinition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public final class TestScenarioDefinitionToStringConverterTest {

    private static final String SIMPLE_SCRIPT = "function run(secondsSinceLatestRun) {}";

    @Test
    public void testConvertToDatabaseColumn() {
        final var testScenarioDefinitionToStringConverter = new TestScenarioDefinitionToStringConverter();

        assertNull(testScenarioDefinitionToStringConverter.convertToDatabaseColumn(null));
        assertEquals(SIMPLE_SCRIPT, testScenarioDefinitionToStringConverter.convertToDatabaseColumn(new TestScenarioDefinition(SIMPLE_SCRIPT)));
    }

    @Test
    public void testConvertToEntityAttribute() {
        final var testScenarioDefinitionToStringConverter = new TestScenarioDefinitionToStringConverter();

        assertNull(testScenarioDefinitionToStringConverter.convertToEntityAttribute(null));
        final var testScenarioDefinition = testScenarioDefinitionToStringConverter.convertToEntityAttribute(SIMPLE_SCRIPT);
        assertNotNull(testScenarioDefinition);
        assertEquals(SIMPLE_SCRIPT, testScenarioDefinition.getScript());
        assertNull(testScenarioDefinition.getCause());
        assertNotNull(testScenarioDefinition.getScriptEngine());
    }

}
