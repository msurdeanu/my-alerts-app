package org.myalerts.model;

import javax.script.ScriptEngine;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Builder
@Getter
@Setter
public class TestScenarioDefinition {

    private final ScriptEngine scriptEngine;

    private final String definition;

    private final String cause;

}
