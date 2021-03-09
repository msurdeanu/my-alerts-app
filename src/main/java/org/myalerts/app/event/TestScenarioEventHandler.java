package org.myalerts.app.event;

import org.myalerts.app.model.TestScenario;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface TestScenarioEventHandler {

    void onActivationChanged(TestScenario testScenario);

    void onCronExpressionChanged(TestScenario testScenario, String newCronExpression);

}
