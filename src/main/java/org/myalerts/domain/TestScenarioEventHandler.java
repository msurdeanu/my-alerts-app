package org.myalerts.domain;

import java.util.Collection;
import java.util.Set;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface TestScenarioEventHandler {

    Collection<TestScenarioResult> getLastResults(TestScenario testScenario);

    void onActivationChanged(TestScenario testScenario);

    void onCronExpressionChanged(TestScenario testScenario, String newCronExpression);

    void onDefinitionChanged(TestScenario testScenario, String newDefinition);

    void onDelete(TestScenario testScenario);

    void onNameChanged(TestScenario testScenario, String newName);

    void onScheduleNow(TestScenario testScenario);

    void onTagsChanged(TestScenario testScenario, Set<String> newTags);

}
