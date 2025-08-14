package org.myalerts.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Builder
@Entity
@Table(name = "results")
@AllArgsConstructor
@NoArgsConstructor
public class TestScenarioResult implements Persistable<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Column(name = "scenario_id")
    private Integer scenarioId;

    @Getter
    private long duration;

    @Getter
    private String cause;

    @Getter
    private Instant created;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return true;
    }

    public static TestScenarioResult of(TestScenarioRun testScenarioRun) {
        return TestScenarioResult.builder()
            .scenarioId(testScenarioRun.getScenarioId())
            .cause(testScenarioRun.getCause())
            .created(testScenarioRun.getCreated())
            .duration(testScenarioRun.getDuration())
            .build();
    }

    public static class TestScenarioResultBuilder {
        public TestScenarioResultBuilder cause(String message) {
            cause = internalizeCause(message);

            return this;
        }

        public TestScenarioResultBuilder cause(Throwable throwable) {
            cause = internalizeCause(ofNullable(throwable)
                .map(item -> ofNullable(item.getCause()).map(Throwable::getMessage).orElseGet(item::getMessage))
                .orElse(null));

            return this;
        }

        private String internalizeCause(String cause) {
            return ofNullable(cause).map(String::intern).orElse(null);
        }
    }

}
