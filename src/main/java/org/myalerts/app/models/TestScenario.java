package org.myalerts.app.models;

import com.github.slugify.Slugify;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;

public class TestScenario implements Runnable {

    @Getter
    @Setter
    private boolean enabled = true;

    @Getter
    @Setter
    private boolean editable = false;

    @Getter
    @Setter
    private boolean failed = false;

    @Getter
    private String name;

    @Getter
    private String id;

    @Getter
    @Setter
    private String cronExpression;

    public void setName(String name) {
        this.name = name;
        id = new Slugify().slugify(name);
    }

    @Override
    public void run() {
        // TODO: implement logic for running current test scenario
    }

    public void toggleOnEnabling() {
        enabled = !enabled;
    }

    public Collection<TestScenarioResult> getFullHistory() {
        // TODO: implement logic for retrieving full history of runs
        return Collections.EMPTY_LIST;
    }
}
