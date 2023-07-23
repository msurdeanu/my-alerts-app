package org.myalerts.domain;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.Getter;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Getter
public final class TestScenarioDefinition {

    private static final GroovyShell GROOVY_SHELL = new GroovyShell();

    private String script;

    private Script parsedScript;

    private String cause;

    public TestScenarioDefinition(final String script) {
        recreateScript(script);
    }

    public boolean recreateScript(final String script) {
        if (script.equals(this.script)) {
            return false;
        }

        this.script = script;
        recreateScriptEngine();

        return true;
    }

    private void recreateScriptEngine() {
        try {
            parsedScript = GROOVY_SHELL.parse(script);
        } catch (Exception e) {
            cause = e.getMessage();
            parsedScript = null;
        }
    }

}
