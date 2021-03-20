package org.myalerts.app.component;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.apache.commons.lang3.StringUtils;
import org.myalerts.app.event.TestScenarioEventHandler;
import org.myalerts.app.interfaces.marker.RequiresUIThread;
import org.myalerts.app.model.TestScenario;
import org.myalerts.app.model.TestScenarioType;
import org.vaadin.klaudeta.PaginatedGrid;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public class TestScenarioGrid extends VerticalLayout {

    private final TestScenarioEventHandler eventHandler;

    private final PaginatedGrid<TestScenario> paginatedGrid = new PaginatedGrid<>();

    private final Binder<TestScenario> testScenarioBinder = new Binder<>(TestScenario.class);

    public TestScenarioGrid(TestScenarioEventHandler eventHandler) {
        this.eventHandler = eventHandler;

        init();
    }

    public void refreshPage() {
        paginatedGrid.refreshPaginator();
    }

    public void setDataProvider(DataProvider<TestScenario, ?> dataProvider) {
        paginatedGrid.setDataProvider(dataProvider);
    }

    private void init() {
        setSizeFull();
        paginatedGrid.setHeightByRows(true);
        paginatedGrid.addColumn(new ComponentRenderer<>(this::renderIsEnabled))
            .setAutoWidth(true);
        paginatedGrid.addColumn(new ComponentRenderer<>(this::renderName))
            .setHeader(getTranslation("test-scenario.main-grid.name.column"))
            .setClassNameGenerator(this::getClassNameForName)
            .setAutoWidth(true);
        paginatedGrid.addColumn(new ComponentRenderer<>(this::renderLastRun))
            .setHeader(getTranslation("test-scenario.main-grid.last-run.column"))
            .setAutoWidth(true);
        paginatedGrid.addColumn(new ComponentRenderer<>(this::renderCronExpression))
            .setHeader(getTranslation("test-scenario.main-grid.cron-expression.column"))
            .setAutoWidth(true);
        paginatedGrid.addColumn(new ComponentRenderer<>(this::renderActions))
            .setHeader(getTranslation("test-scenario.main-grid.actions.column"))
            .setAutoWidth(true);
        paginatedGrid.setPageSize(10);
        paginatedGrid.setPaginatorSize(5);

        add(paginatedGrid);
    }

    @RequiresUIThread
    private Component renderIsEnabled(TestScenario testScenario) {
        final ToggleButton toggleButton = new ToggleButton(testScenario.isEnabled());
        toggleButton.addValueChangeListener(event -> eventHandler.onActivationChanged(testScenario));
        return toggleButton;
    }

    @RequiresUIThread
    private Component renderName(TestScenario testScenario) {
        return new Label(testScenario.getName());
    }

    @RequiresUIThread
    private String getClassNameForName(TestScenario testScenario) {
        if (!testScenario.isEnabled()) {
            return TestScenarioType.DISABLED.getLabelAsLowercase();
        }

        return testScenario.isFailed() ? TestScenarioType.FAILED.getLabelAsLowercase() : TestScenarioType.PASSED.getLabelAsLowercase();
    }

    @RequiresUIThread
    private Component renderLastRun(TestScenario testScenario) {
        final Button lastRunButton = new Button(getTranslation("test-scenario.main-grid.not-available"));
        lastRunButton.addClickListener(event -> new TestScenarioHistoryDialog(testScenario.getFullHistory()).open());
        return lastRunButton;
    }

    @RequiresUIThread
    private Component renderCronExpression(TestScenario testScenario) {
        if (!testScenario.isEditable()) {
            return new Label(testScenario.getCron());
        }

        TextField textField = new TextField();
        testScenarioBinder.forField(textField)
            .withValidator(cron -> true, StringUtils.EMPTY)
            .bind(TestScenario::getCron, (Setter<TestScenario, String>) eventHandler::onCronExpressionChanged);
        testScenarioBinder.setBean(testScenario);

        textField.addKeyUpListener(Key.ENTER, event -> onCronExpressionUpdated(testScenario));
        textField.addBlurListener(event -> onCronExpressionCancelled(testScenario));

        return textField;
    }

    @RequiresUIThread
    private Component renderActions(TestScenario testScenario) {
        final HorizontalLayout horizontalLayout = new HorizontalLayout();

        final Button editButton = new Button(VaadinIcon.EDIT.create());
        editButton.addClickListener(event -> onCronExpressionToEdit(testScenario));

        horizontalLayout.add(editButton);

        return horizontalLayout;
    }

    private void onCronExpressionToEdit(TestScenario testScenario) {
        testScenario.setEditable(true);
        paginatedGrid.getDataProvider().refreshItem(testScenario);
    }

    private void onCronExpressionUpdated(TestScenario testScenario) {
        paginatedGrid.getDataProvider().refreshItem(testScenario);
    }

    private void onCronExpressionCancelled(TestScenario testScenario) {
        testScenario.setEditable(false);
        paginatedGrid.getDataProvider().refreshItem(testScenario);
    }

}
