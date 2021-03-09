package org.myalerts.app.event;

import lombok.Builder;
import lombok.Getter;
import org.myalerts.app.model.TestScenario;
import org.myalerts.app.model.TestScenarioResult;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Builder
@Getter
public class TestResultEvent implements Event {

    private final TestScenario testScenario;

    private final TestScenarioResult testScenarioResult;

}
