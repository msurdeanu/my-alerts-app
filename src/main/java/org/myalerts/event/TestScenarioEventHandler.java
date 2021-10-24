package org.myalerts.event;

import java.util.Collection;

import org.myalerts.model.TestScenario;
import org.myalerts.model.TestScenarioResult;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface TestScenarioEventHandler {

    Collection<TestScenarioResult> getLastResults(final TestScenario testScenario);

    void onActivationChanged(final TestScenario testScenario);

    void onCronExpressionChanged(final TestScenario testScenario, final String newCronExpression);

    void onDelete(final TestScenario testScenario);

    void onScheduleNow(final TestScenario testScenario);

}
