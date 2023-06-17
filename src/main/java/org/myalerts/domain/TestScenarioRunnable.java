package org.myalerts.domain;

import groovy.lang.Script;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.myalerts.ApplicationManager;
import org.myalerts.domain.event.TestScenarioRunEvent;
import org.myalerts.exception.AlertingRuntimeException;
import org.myalerts.provider.HelpersProvider;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.time.Instant;
import java.time.temporal.Temporal;

import static java.time.Duration.between;
import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RequiredArgsConstructor
public final class TestScenarioRunnable implements Runnable {

    private final ApplicationManager applicationManager;

    @Getter
    private final TestScenario testScenario;

    @Override
    public void run() {
        final var testScenarioDefinition = testScenario.getDefinition();
        final var nextLastRunTime = Instant.now();
        final var testScenarioRunBuilder = TestScenarioRun.builder()
            .scenarioId(testScenario.getId())
            .scenarioName(testScenario.getName())
            .scenarioTags(testScenario.getTagsAsString());
        final var executionContext = ExecutionContext.builder()
            .testScenarioRunBuilder(testScenarioRunBuilder)
            .millisSinceLatestRun(getMillisBetween(testScenario.getLastRunTime(), nextLastRunTime))
            .build();

        final var startTime = System.currentTimeMillis();
        try {
            invokeRunMethod(ofNullable(testScenarioDefinition.getParsedScript())
                .orElseThrow(() -> new AlertingRuntimeException(testScenarioDefinition.getCause())), executionContext);
        } catch (Throwable throwable) {
            executionContext.markAsFailed(throwable);
        } finally {
            testScenario.setFailed(executionContext.isMarkedAsFailed());
            testScenario.setLastRunTime(nextLastRunTime);

            applicationManager.getEventBroadcaster().broadcast(TestScenarioRunEvent.builder()
                .testScenarioRun(testScenarioRunBuilder
                    .duration(System.currentTimeMillis() - startTime)
                    .created(Instant.from(nextLastRunTime))
                    .build())
                .build());
        }
    }

    private long getMillisBetween(@Null final Temporal startInclusive,
                                  @NotNull final Temporal endExclusive) {
        return between(ofNullable(startInclusive).orElseGet(() -> Instant.ofEpochSecond(0)), endExclusive).toMillis();
    }

    private void invokeRunMethod(final Script parsedScript,
                                 final Object... functionArgs) {
        try {
            applicationManager.getBeansOfTypeAsStream(HelpersProvider.class)
                .flatMap(provider -> provider.getTestScenarioRunProperties().stream())
                .forEach(property -> parsedScript.setProperty(property.getName(), property.getValue()));
            parsedScript.invokeMethod("run", functionArgs);
        } catch (Exception e) {
            throw new AlertingRuntimeException(e);
        }
    }

}
