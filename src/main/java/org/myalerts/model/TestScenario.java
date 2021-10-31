package org.myalerts.model;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SelectBeforeUpdate;

import org.myalerts.converter.TestScenarioDefinitionToStringConverter;
import org.myalerts.event.EventBroadcaster;
import org.myalerts.event.TestDeleteEvent;
import org.myalerts.event.TestResultEvent;
import org.myalerts.event.TestUpdateEvent;
import org.myalerts.exception.AlertingException;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Entity
@SelectBeforeUpdate(value = false)
@Table(name = "scenarios")
public class TestScenario implements Runnable {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    private boolean enabled;

    @Getter
    private String name;

    @Getter
    private String cron;

    @Getter
    @Convert(converter = TestScenarioDefinitionToStringConverter.class)
    private TestScenarioDefinition definition;

    @Getter
    @Column
    private Instant lastRunTime;

    @Getter
    @Column
    private boolean failed = false;

    @Getter
    @Setter
    @Transient
    private boolean editable = false;

    public void setName(final String name) {
        this.name = name;

        EventBroadcaster.broadcast(TestUpdateEvent.builder().testScenario(this).build());
    }

    public void setCron(final String cron) {
        this.cron = cron;

        EventBroadcaster.broadcast(TestUpdateEvent.builder().testScenario(this).build());
    }

    public void setDefinition(final String definition) {
        this.definition.recreate(definition);

        EventBroadcaster.broadcast(TestUpdateEvent.builder().testScenario(this).build());
    }

    public void toggleOnEnabling() {
        enabled = !enabled;

        EventBroadcaster.broadcast(TestUpdateEvent.builder().testScenario(this).build());
    }

    public void markAsDeleted() {
        EventBroadcaster.broadcast(TestDeleteEvent.builder().testScenario(this).build());
    }

    @Override
    public void run() {
        Object result = null;
        Throwable throwable = null;
        final Instant nextLastRunTime = Instant.now();
        final long start = System.currentTimeMillis();
        try {
            result = invokeExecute(ofNullable(definition.getScriptEngine())
                .orElseThrow(() -> new AlertingException(definition.getCause())), getSecondsBetween(lastRunTime, nextLastRunTime));
            failed = false;
        } catch (Throwable t) {
            throwable = t;
            failed = true;
        } finally {
            lastRunTime = nextLastRunTime;
            final TestScenarioResult.TestScenarioResultBuilder testScenarioResultBuilder = TestScenarioResult.builder()
                .scenarioId(id)
                .duration(System.currentTimeMillis() - start)
                .cause(throwable)
                .created(Instant.from(lastRunTime));
            ofNullable(result).map(Object::toString).ifPresent(testScenarioResultBuilder::cause);

            EventBroadcaster.broadcast(TestResultEvent.builder().testScenario(this).testScenarioResult(testScenarioResultBuilder.build()).build());
        }
    }

    private Object invokeExecute(final ScriptEngine scriptEngine, final Object... functionArgs) {
        try {
            return ((Invocable) scriptEngine).invokeFunction("execute", functionArgs);
        } catch (Exception e) {
            throw new AlertingException(e);
        }
    }

    private long getSecondsBetween(@Null final Temporal startInclusive, @NotNull final Temporal endExclusive) {
        return Duration.between(ofNullable(startInclusive).orElseGet(() -> Instant.ofEpochSecond(0)), endExclusive).toSeconds();
    }

}
