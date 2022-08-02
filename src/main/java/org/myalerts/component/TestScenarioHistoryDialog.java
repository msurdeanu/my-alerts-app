package org.myalerts.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.myalerts.domain.TestScenarioResult;
import org.myalerts.provider.CustomI18NProvider;

import java.util.Collection;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public final class TestScenarioHistoryDialog extends ResponsiveDialog {

    public TestScenarioHistoryDialog(final Supplier<Collection<TestScenarioResult>> testScenarioResultsSupplier) {
        super("testScenarioHistory");

        add(createResultGrid(testScenarioResultsSupplier));
    }

    private Grid<TestScenarioResult> createResultGrid(final Supplier<Collection<TestScenarioResult>> testScenarioResultsSupplier) {
        final var grid = new Grid<TestScenarioResult>();
        grid.setSizeFull();
        grid.setItems(testScenarioResultsSupplier.get());
        grid.addColumn(new ComponentRenderer<>(this::renderRunTime))
            .setHeader(getTranslation("test-scenario.history.run-time.column"))
            .setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::renderDuration))
            .setHeader(getTranslation("test-scenario.history.duration.column"))
            .setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::renderResult))
            .setHeader(getTranslation("test-scenario.history.result.column"))
            .setAutoWidth(true);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        return grid;
    }

    private Component renderRunTime(final TestScenarioResult testScenarioResult) {
        return new Label(getTranslation(CustomI18NProvider.PRETTY_TIME_FORMAT, testScenarioResult.getCreated()));
    }

    private Component renderDuration(final TestScenarioResult testScenarioResult) {
        return new Label(getTranslation("test-scenario.history.duration-ms", testScenarioResult.getDuration()));
    }

    private Component renderResult(final TestScenarioResult testScenarioResult) {
        return ofNullable(testScenarioResult.getCause())
            .map(this::mapToTextArea)
            .orElseGet(() -> new Label(getTranslation("test-scenario.history.no-failure-detected")));
    }

    private Component mapToTextArea(final String value) {
        final var resultTextArea = new TextArea();
        resultTextArea.setReadOnly(true);
        resultTextArea.setWidth("100%");
        resultTextArea.setValue(value);
        resultTextArea.setMaxHeight("120px");
        return resultTextArea;
    }

}
