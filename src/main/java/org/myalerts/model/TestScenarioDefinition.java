package org.myalerts.model;

import java.util.function.Predicate;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import lombok.Getter;

import org.myalerts.helper.HttpRequestHelper;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Getter
public class TestScenarioDefinition {

    private static final Predicate<String> CLASS_IN_HELPER_PACKAGE = className -> className.startsWith(HttpRequestHelper.class.getPackageName());

    private String script;

    private ScriptEngine scriptEngine;

    private String cause;

    public TestScenarioDefinition(final String script) {
        recreateScript(script);
    }

    public void recreateScript(final String script) {
        this.script = script;

        recreateScriptEngine();
    }

    private void recreateScriptEngine() {
        try {
            scriptEngine = new ScriptEngineManager().getEngineByName("graal.js");
            final var bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("polyglot.js.allowHostAccess", true);
            bindings.put("polyglot.js.allowHostClassLookup", CLASS_IN_HELPER_PACKAGE);
            scriptEngine.eval(script);
        } catch (Exception e) {
            cause = e.getMessage();
            scriptEngine = null;
        }
    }

}
