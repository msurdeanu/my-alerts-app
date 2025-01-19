package org.myalerts.view.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.myalerts.domain.TestScenarioResult;
import org.myalerts.provider.TranslationProvider;

import java.util.Collection;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public final class TestScenarioHistoryDialog extends ResponsiveDialog {

    public TestScenarioHistoryDialog(Supplier<Collection<TestScenarioResult>> testScenarioResultsSupplier) {
        super("testScenarioHistory");

        add(createResultGrid(testScenarioResultsSupplier));
    }

    private Grid<TestScenarioResult> createResultGrid(Supplier<Collection<TestScenarioResult>> testScenarioResultsSupplier) {
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

    private Component renderRunTime(TestScenarioResult testScenarioResult) {
        return new NativeLabel(getTranslation(TranslationProvider.PRETTY_TIME_FORMAT, testScenarioResult.getCreated()));
    }

    private Component renderDuration(TestScenarioResult testScenarioResult) {
        return new NativeLabel(getTranslation("test-scenario.history.duration-ms", testScenarioResult.getDuration()));
    }

    private Component renderResult(TestScenarioResult testScenarioResult) {
        return ofNullable(testScenarioResult.getCause())
            .map(this::mapToTextArea)
            .orElseGet(() -> new NativeLabel(getTranslation("test-scenario.history.no-failure-detected")));
    }

    private Component mapToTextArea(String value) {
        final var resultTextArea = new TextArea();
        resultTextArea.setReadOnly(true);
        resultTextArea.setWidthFull();
        resultTextArea.setValue(value);
        resultTextArea.setMaxHeight("120px");
        return resultTextArea;
    }

}
