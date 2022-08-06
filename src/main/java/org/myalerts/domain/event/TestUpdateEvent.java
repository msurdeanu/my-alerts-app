package org.myalerts.domain.event;

import lombok.Builder;
import lombok.Getter;
import org.myalerts.domain.TestScenario;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Builder
@Getter
public class TestUpdateEvent implements Event {

    private final TestScenario testScenario;

}
