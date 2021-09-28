package org.myalerts.app.model;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import lombok.Getter;
import lombok.Setter;

import org.myalerts.app.event.EventBroadcaster;
import org.myalerts.app.event.TestResultEvent;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Entity
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
    private String definition;

    @Transient
    @Getter
    @Setter
    private boolean editable = false;

    @Transient
    @Getter
    private boolean failed = false;

    @Transient
    @Getter
    private boolean dirtyFlag = false;

    @Override
    public void run() {
        Object result = null;
        Throwable throwable = null;
        final long start = System.currentTimeMillis();
        try {
            final ScriptEngine engine = new ScriptEngineManager().getEngineByName("graal.js");
            engine.eval(definition);
            final Invocable invocable = (Invocable) engine;
            result = invocable.invokeFunction("execute", 0);
            failed = false;
        } catch (Throwable t) {
            throwable = t;
            failed = true;
        } finally {
            final TestScenarioResult.TestScenarioResultBuilder testScenarioResultBuilder = TestScenarioResult.builder()
                .scenarioId(id)
                .duration(System.currentTimeMillis() - start)
                .cause(throwable)
                .created(Instant.now());
            Optional.ofNullable(result).map(Object::toString).ifPresent(testScenarioResultBuilder::cause);

            EventBroadcaster.broadcast(TestResultEvent.builder().testScenario(this).testScenarioResult(testScenarioResultBuilder.build()).build());
        }
    }

    public void setCron(String cron) {
        this.cron = cron;
        dirtyFlag = true;
    }

    public void toggleOnEnabling() {
        enabled = !enabled;
        dirtyFlag = true;
    }

    public void resetDirtyFlag() {
        dirtyFlag = false;
    }

    public Collection<TestScenarioResult> getFullHistory() {
        // TODO: implement logic for retrieving full history of runs
        return Collections.emptyList();
    }

}
