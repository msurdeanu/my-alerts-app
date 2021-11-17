package org.myalerts.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.myalerts.model.TestScenarioDefinition;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Converter
public class TestScenarioDefinitionToStringConverter implements AttributeConverter<TestScenarioDefinition, String> {


    @Override
    public String convertToDatabaseColumn(final TestScenarioDefinition attribute) {
        return ofNullable(attribute).map(TestScenarioDefinition::getScript).orElse(null);
    }

    @Override
    public TestScenarioDefinition convertToEntityAttribute(final String data) {
        return ofNullable(data).map(this::mapToTestScenarioDefinition).orElse(null);
    }

    private TestScenarioDefinition mapToTestScenarioDefinition(final String definition) {
        return new TestScenarioDefinition(definition);
    }

}
