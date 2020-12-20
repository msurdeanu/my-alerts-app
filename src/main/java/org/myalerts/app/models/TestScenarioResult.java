package org.myalerts.app.models;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

public class TestScenarioResult {

    @Getter
    @Setter
    private Instant runTime;

    @Getter
    @Setter
    private long durationInMillis;

    @Getter
    @Setter
    private String failingCause;
}
