package org.myalerts.domain;

import groovy.lang.Script;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.SortNatural;
import org.myalerts.ApplicationContext;
import org.myalerts.converter.TestScenarioDefinitionToStringConverter;
import org.myalerts.domain.event.TestDeleteEvent;
import org.myalerts.domain.event.TestResultEvent;
import org.myalerts.domain.event.TestUpdateEvent;
import org.myalerts.exception.AlertingException;

import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

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
    @Convert(converter = TestScenarioDefinitionToStringConverter.class)
    private TestScenarioDefinition definition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinFormula("(SELECT r.id FROM results r WHERE r.scenario_id = id ORDER BY r.created DESC LIMIT 1)")
    private TestScenarioResult latestResult;

    @Getter
    @ManyToMany(cascade = {
        CascadeType.PERSIST,
        CascadeType.MERGE
    }, fetch = FetchType.EAGER)
    @JoinTable(name = "scenarios_tags",
        joinColumns = @JoinColumn(name = "scenario_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @SortNatural
    private SortedSet<Tag> tags = new TreeSet<>();

    @Transient
    private Instant lastRunTime;

    @Transient
    private Boolean failed;

    @Getter
    @Setter
    @Transient
    private boolean editable = false;

    @Setter
    @Transient
    private ApplicationContext applicationContext;

    public Instant getLastRunTime() {
        lastRunTime = ofNullable(lastRunTime)
            .orElseGet(() -> ofNullable(latestResult).map(TestScenarioResult::getCreated).orElse(null));
        return lastRunTime;
    }

    public String getTagsSeparatedByComma() {
        return tags.stream().map(Tag::getName).collect(Collectors.joining(","));
    }

    public boolean isFailed() {
        failed = ofNullable(failed)
            .orElseGet(() -> ofNullable(latestResult).map(result -> result.getCause() != null).orElse(null));
        return Boolean.TRUE.equals(failed);
    }

    public void setName(final String name) {
        this.name = name;

        ofNullable(applicationContext).ifPresent(context -> context.getEventBroadcaster()
            .broadcast(TestUpdateEvent.builder().testScenario(this).build()));
    }

    public void setTags(final String newTagsSeparatedByComma) {
        // TODO: remove tag if needed
        Arrays.stream(newTagsSeparatedByComma.split(","))
            .map(String::trim)
            .filter(item -> tags.stream().noneMatch(tag -> tag.getName().equals(item)))
            .forEach(item -> tags.add(new Tag(item)));

        ofNullable(applicationContext).ifPresent(context -> context.getEventBroadcaster()
            .broadcast(TestUpdateEvent.builder().testScenario(this).build()));
    }

    public void setCron(final String cron) {
        this.cron = cron;

        ofNullable(applicationContext).ifPresent(context -> context.getEventBroadcaster()
            .broadcast(TestUpdateEvent.builder().testScenario(this).build()));
    }

    public void setScript(final String scrip) {
        this.definition.recreateScript(scrip);

        ofNullable(applicationContext).ifPresent(context -> context.getEventBroadcaster()
            .broadcast(TestUpdateEvent.builder().testScenario(this).build()));
    }

    public void toggleOnEnabling() {
        enabled = !enabled;

        ofNullable(applicationContext).ifPresent(context -> context.getEventBroadcaster()
            .broadcast(TestUpdateEvent.builder().testScenario(this).build()));
    }

    public void toggleOnEditing() {
        editable = !editable;
    }

    public void markAsDeleted() {
        ofNullable(applicationContext).ifPresent(context -> context.getEventBroadcaster()
            .broadcast(TestDeleteEvent.builder().testScenario(this).build()));
    }

    @Override
    public void run() {
        final var nextLastRunTime = Instant.now();
        final var testScenarioResultBuilder = TestScenarioResult.builder()
            .scenarioId(id);
        final var executionContext = ExecutionContext.builder()
            .applicationContext(applicationContext)
            .testScenarioResultBuilder(testScenarioResultBuilder)
            .millisSinceLatestRun(getMillisBetween(getLastRunTime(), nextLastRunTime))
            .build();

        final var startTime = System.currentTimeMillis();
        try {
            invokeRunMethod(ofNullable(definition.getParsedScript())
                .orElseThrow(() -> new AlertingException(definition.getCause())), executionContext);
        } catch (Throwable throwable) {
            executionContext.markAsFailed(throwable);
        } finally {
            failed = executionContext.isMarkedAsFailed();
            lastRunTime = nextLastRunTime;

            ofNullable(applicationContext).ifPresent(context -> context.getEventBroadcaster()
                .broadcast(TestResultEvent.builder()
                    .testScenario(this)
                    .testScenarioResult(testScenarioResultBuilder
                        .duration(System.currentTimeMillis() - startTime)
                        .created(Instant.from(lastRunTime))
                        .build())
                    .build()));
        }
    }

    private void invokeRunMethod(final Script parsedScript,
                                 final Object... functionArgs) {
        try {
            parsedScript.invokeMethod("run", functionArgs);
        } catch (Exception e) {
            throw new AlertingException(e);
        }
    }

    private long getMillisBetween(@Null final Temporal startInclusive,
                                  @NotNull final Temporal endExclusive) {
        return Duration.between(ofNullable(startInclusive).orElseGet(() -> Instant.ofEpochSecond(0)), endExclusive).toMillis();
    }

}
