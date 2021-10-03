package org.myalerts.app.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.myalerts.app.model.TestScenarioDefinition;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Converter
public class TestScenarioDefinitionToStringConverter implements AttributeConverter<TestScenarioDefinition, String> {

    @Override
    public String convertToDatabaseColumn(final TestScenarioDefinition attribute) {
        return ofNullable(attribute).map(TestScenarioDefinition::getDefinition).orElse(null);
    }

    @Override
    public TestScenarioDefinition convertToEntityAttribute(final String data) {
        return ofNullable(data).map(this::mapToTestScenarioDefinition).orElse(null);
    }

    private TestScenarioDefinition mapToTestScenarioDefinition(final String definition) {
        final TestScenarioDefinition.TestScenarioDefinitionBuilder testScenarioDefinitionBuilder = TestScenarioDefinition.builder();
        testScenarioDefinitionBuilder.definition(definition);

        try {
            final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("graal.js");
            scriptEngine.eval(definition);
            testScenarioDefinitionBuilder.scriptEngine(scriptEngine);
        } catch (Exception e) {
            testScenarioDefinitionBuilder.cause(e.getMessage());
        }

        return testScenarioDefinitionBuilder.build();
    }

}
