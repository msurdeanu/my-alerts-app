package org.myalerts.app.component;

import java.util.Collection;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.myalerts.app.interfaces.marker.RequiresUIThread;
import org.myalerts.app.model.TestScenarioResult;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public class TestScenarioHistoryDialog extends Dialog {

    private static final int MAX_WIDTH = 900;
    private static final int MAX_HEIGHT = 450;

    private static final String PIXELS = "px";

    public TestScenarioHistoryDialog(Collection<TestScenarioResult> testScenarioResults) {
        add(createResultGrid(testScenarioResults));

        makeDialogResizableAndDraggable();
        makeDialogResponsive();
    }

    private Grid<TestScenarioResult> createResultGrid(Collection<TestScenarioResult> testScenarioResults) {
        new Label(getTranslation("millis", 10));
        Grid<TestScenarioResult> testScenarioResultGrid = new Grid<>();
        testScenarioResultGrid.setSizeFull();
        testScenarioResultGrid.setItems(testScenarioResults);
        testScenarioResultGrid.addColumn(new ComponentRenderer<>(this::renderRunTime))
            .setHeader(getTranslation("test-scenario.history.run-time.column"))
            .setAutoWidth(true);
        testScenarioResultGrid.addColumn(new ComponentRenderer<>(this::renderDuration))
            .setHeader(getTranslation("test-scenario.history.duration.column"))
            .setAutoWidth(true);
        testScenarioResultGrid.addColumn(new ComponentRenderer<>(this::renderResult))
            .setHeader(getTranslation("test-scenario.history.result.column"))
            .setAutoWidth(true);
        return testScenarioResultGrid;
    }

    @RequiresUIThread
    private Component renderRunTime(TestScenarioResult testScenarioResult) {
        return new Label(testScenarioResult.getCreated().toString());
    }

    @RequiresUIThread
    private Component renderDuration(TestScenarioResult testScenarioResult) {
        return new Label(getTranslation("test-scenario.history.duration-ms", testScenarioResult.getDuration()));
    }

    @RequiresUIThread
    private Component renderResult(TestScenarioResult testScenarioResult) {
        return Optional.ofNullable(testScenarioResult.getCause())
            .map(cause -> {
                final TextArea resultTextArea = new TextArea();
                resultTextArea.setReadOnly(true);
                resultTextArea.setWidth("100%");
                resultTextArea.setValue(cause);
                return (Component) resultTextArea;
            })
            .orElseGet(() -> new Label(getTranslation("test-scenario.history.no-failure-detected")));
    }

    @RequiresUIThread
    private void makeDialogResizableAndDraggable() {
        setDraggable(true);
        setResizable(true);
    }

    @RequiresUIThread
    private void makeDialogResponsive() {
        UI.getCurrent().getPage().retrieveExtendedClientDetails(details -> {
            setWidth(Math.max(MAX_WIDTH, details.getScreenWidth() / 2) + PIXELS);
            setHeight(Math.max(MAX_HEIGHT, details.getScreenHeight() / 2) + PIXELS);
        });

        UI.getCurrent().getPage().addBrowserWindowResizeListener(details -> {
            setWidth(Math.max(MAX_WIDTH, details.getWidth() / 2) + PIXELS);
            setHeight(Math.max(MAX_HEIGHT, details.getHeight() / 2) + PIXELS);
        });
    }

}
