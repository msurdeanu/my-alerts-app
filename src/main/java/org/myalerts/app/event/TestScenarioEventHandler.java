package org.myalerts.app.event;

import java.util.Collection;

import org.myalerts.app.model.TestScenario;
import org.myalerts.app.model.TestScenarioResult;

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
