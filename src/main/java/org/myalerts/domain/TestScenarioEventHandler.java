package org.myalerts.domain;

import java.util.Collection;
import java.util.Set;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface TestScenarioEventHandler {

    Collection<TestScenarioResult> getLastResults(final TestScenario testScenario);

    void onActivationChanged(final TestScenario testScenario);

    void onCronExpressionChanged(final TestScenario testScenario, final String newCronExpression);

    void onDefinitionChanged(final TestScenario testScenario, final String newDefinition);

    void onDelete(final TestScenario testScenario);

    void onNameChanged(final TestScenario testScenario, final String newName);

    void onScheduleNow(final TestScenario testScenario);

    void onTagsChanged(final TestScenario testScenario, final Set<String> newTags);

}
