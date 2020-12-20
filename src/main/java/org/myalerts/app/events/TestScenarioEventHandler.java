package org.myalerts.app.events;

import org.myalerts.app.models.TestScenario;

public interface TestScenarioEventHandler {

    void onActivationChanged(TestScenario testScenario);

    void onCronExpressionChanged(TestScenario testScenario, String newCronExpression);

}
