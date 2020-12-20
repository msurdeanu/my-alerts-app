package org.myalerts.app.events;

import lombok.Builder;
import lombok.Getter;
import org.myalerts.app.models.TestScenarioResult;
import org.myalerts.app.models.TestScenario;

@Builder
@Getter
public class TestResultEvent implements Event {

    private final TestScenario testScenario;

    private final TestScenarioResult testScenarioResult;

}
