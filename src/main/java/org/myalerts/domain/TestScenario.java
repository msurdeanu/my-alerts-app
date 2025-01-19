package org.myalerts.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.SortNatural;
import org.myalerts.converter.TestScenarioDefinitionToStringConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Entity
@Table(name = "scenarios")
public class TestScenario {

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

    @Setter
    @Transient
    private Instant lastRunTime;

    @Setter
    @Transient
    private Boolean failed;

    @Getter
    @Setter
    @Transient
    private boolean editable = false;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final var testScenario = (TestScenario) other;
        return Objects.equals(id, testScenario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Instant getLastRunTime() {
        lastRunTime = ofNullable(lastRunTime)
            .orElseGet(() -> ofNullable(latestResult).map(TestScenarioResult::getCreated).orElse(null));
        return lastRunTime;
    }

    public Set<String> getTagsAsString() {
        return tags.stream().map(Tag::getName).collect(Collectors.toSet());
    }

    public boolean isFailed() {
        failed = ofNullable(failed)
            .orElseGet(() -> ofNullable(latestResult).map(result -> result.getCause() != null).orElse(null));
        return Boolean.TRUE.equals(failed);
    }

    public boolean addTags(Set<Tag> tags) {
        tags.forEach(tag -> {
            this.tags.add(tag);
            tag.getTestScenarios().add(this);
        });
        return true;
    }

    public boolean removeTagIf(Predicate<? super Tag> filter) {
        var removed = false;
        var eachTag = tags.iterator();
        while (eachTag.hasNext()) {
            final var eligibleForRemoval = eachTag.next();
            if (filter.test(eligibleForRemoval)) {
                eachTag.remove();
                eligibleForRemoval.getTestScenarios().remove(this);
                removed = true;
            }
        }
        return removed;
    }

    public boolean setCron(String cron) {
        if (cron.equals(this.cron)) {
            return false;
        }

        this.cron = cron;
        return true;
    }

    public boolean setName(String name) {
        if (name.equals(this.name)) {
            return false;
        }

        this.name = name;
        return true;
    }

    public boolean setScript(String script) {
        return definition.recreateScript(script);
    }

    public void toggleOnEnabling() {
        enabled = !enabled;
    }

    public void toggleOnEditing() {
        editable = !editable;
    }

}
