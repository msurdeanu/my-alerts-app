package org.myalerts.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myalerts.api.domain.TestScenarioRun;
import org.springframework.data.domain.Persistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Optional;

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

    public static TestScenarioResult from(final TestScenarioRun testScenarioRun) {
        return TestScenarioResult.builder()
            .scenarioId(testScenarioRun.getScenarioId())
            .cause(testScenarioRun.getCause())
            .created(testScenarioRun.getCreated())
            .duration(testScenarioRun.getDuration())
            .build();
    }

    public static class TestScenarioResultBuilder {
        public TestScenarioResultBuilder cause(final String message) {
            cause = message;

            return this;
        }

        public TestScenarioResultBuilder cause(final Throwable throwable) {
            cause = Optional.ofNullable(throwable)
                .map(item -> Optional.ofNullable(item.getCause()).map(Throwable::getMessage).orElseGet(item::getMessage))
                .orElse(null);

            return this;
        }
    }

}
