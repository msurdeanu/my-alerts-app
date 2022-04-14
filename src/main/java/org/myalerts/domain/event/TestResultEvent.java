package org.myalerts.domain.event;

import lombok.Builder;
import lombok.Getter;

import org.myalerts.domain.TestScenario;
import org.myalerts.domain.TestScenarioResult;

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
