package org.myalerts.converter;

import java.util.function.Predicate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;

import lombok.extern.slf4j.Slf4j;

import org.myalerts.helper.HttpRequestHelper;
import org.myalerts.model.TestScenarioDefinition;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Converter
public class TestScenarioDefinitionToStringConverter implements AttributeConverter<TestScenarioDefinition, String> {

    private static final Predicate<String> CLASS_IN_HELPER_PACKAGE = className -> className.startsWith(HttpRequestHelper.class.getPackageName());

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
            final var scriptEngine = new ScriptEngineManager().getEngineByName("graal.js");
            final var bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("polyglot.js.allowHostAccess", true);
            bindings.put("polyglot.js.allowHostClassLookup", CLASS_IN_HELPER_PACKAGE);
            scriptEngine.eval(definition);
            testScenarioDefinitionBuilder.scriptEngine(scriptEngine);
        } catch (Exception e) {
            testScenarioDefinitionBuilder.cause(e.getMessage());
        }

        return testScenarioDefinitionBuilder.build();
    }

}
