package org.myalerts.app.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Builder
@Entity
@Table(name = "results")
@AllArgsConstructor
@NoArgsConstructor
public class TestScenarioResult {

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

    public static class TestScenarioResultBuilder {
        public TestScenarioResultBuilder cause(Throwable throwable) {
            // TODO: extract more details about throwable
            cause = throwable.getMessage();

            return this;
        }
    }

}