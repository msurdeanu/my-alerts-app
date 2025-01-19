package org.myalerts.domain;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Builder
public final class ExecutionContext {

    @Getter
    private final long millisSinceLatestRun;

    private final TestScenarioRun.TestScenarioRunBuilder testScenarioRunBuilder;

    @Getter
    private boolean markedAsFailed;

    public void markAsFailed(String message) {
        markedAsFailed = true;
        testScenarioRunBuilder.cause(message);
    }

    public void markAsFailed(Throwable throwable) {
        markedAsFailed = true;
        testScenarioRunBuilder.cause(throwable);
    }

}
