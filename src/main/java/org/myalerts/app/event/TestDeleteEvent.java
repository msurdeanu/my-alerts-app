package org.myalerts.app.event;

import lombok.Builder;
import lombok.Getter;

import org.myalerts.app.model.TestScenario;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Builder
@Getter
public class TestDeleteEvent implements Event {

    private final TestScenario testScenario;

}
