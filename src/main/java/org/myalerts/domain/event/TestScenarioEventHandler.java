package org.myalerts.domain.event;

import java.util.Collection;

import org.myalerts.domain.TestScenario;
import org.myalerts.domain.TestScenarioResult;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface TestScenarioEventHandler {

    Collection<TestScenarioResult> getLastResults(final TestScenario testScenario);

    void onActivationChanged(final TestScenario testScenario);

    void onCronExpressionChanged(final TestScenario testScenario, final String newCronExpression);

    void onNameChanged(final TestScenario testScenario, final String newName);

    void onTagsChanged(final TestScenario testScenario, final String newTagsSeparatedByComma);

    void onDefinitionChanged(final TestScenario testScenario, final String newDefinition);

    void onDelete(final TestScenario testScenario);

    void onScheduleNow(final TestScenario testScenario);

}
