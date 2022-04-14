package org.myalerts.domain;

import lombok.Builder;
import lombok.Getter;
import org.myalerts.ApplicationContext;
import org.myalerts.EventBroadcaster;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Builder
public final class ExecutionContext {

    private final ApplicationContext applicationContext;

    @Getter
    private final long millisSinceLatestRun;

    private final TestScenarioResult.TestScenarioResultBuilder testScenarioResultBuilder;

    @Getter
    private boolean markedAsFailed;

    public EventBroadcaster getEventBroadcaster() {
        return applicationContext.getEventBroadcaster();
    }

    public void markAsFailed(final String message) {
        markedAsFailed = true;
        testScenarioResultBuilder.cause(message);
    }

    public void markAsFailed(final Throwable throwable) {
        markedAsFailed = true;
        testScenarioResultBuilder.cause(throwable);
    }

}
