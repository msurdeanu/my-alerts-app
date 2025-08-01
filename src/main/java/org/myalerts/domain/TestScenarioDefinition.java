package org.myalerts.domain;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.Getter;
import org.myalerts.holder.ParentClassLoaderHolder;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Getter
public final class TestScenarioDefinition {

    private String script;

    private Script parsedScript;

    private String cause;

    public TestScenarioDefinition(String script) {
        recreateScript(script);
    }

    public boolean recreateScript(String script) {
        if (script.equals(this.script)) {
            return false;
        }

        this.script = script;
        recreateScriptEngine();

        return true;
    }

    private void recreateScriptEngine() {
        try {
            parsedScript = new GroovyShell(ParentClassLoaderHolder.INSTANCE.getClassLoader()).parse(script);
        } catch (Exception e) {
            cause = e.getMessage();
            parsedScript = null;
        }
    }

}
