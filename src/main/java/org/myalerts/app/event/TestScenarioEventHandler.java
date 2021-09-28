package org.myalerts.app.event;

import org.myalerts.app.model.TestScenario;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface TestScenarioEventHandler {

    void onActivationChanged(final TestScenario testScenario);

    void onCronExpressionChanged(final TestScenario testScenario, final String newCronExpression);

    void onDelete(final TestScenario testScenario);

    void onScheduleNow(final TestScenario testScenario);

}
