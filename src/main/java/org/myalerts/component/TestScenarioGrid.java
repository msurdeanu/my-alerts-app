package org.myalerts.component;

import java.util.Optional;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteParameters;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.klaudeta.PaginatedGrid;

import org.myalerts.event.TestScenarioEventHandler;
import org.myalerts.marker.RequiresUIThread;
import org.myalerts.model.TestScenario;
import org.myalerts.model.TestScenarioType;
import org.myalerts.model.UserRole;
import org.myalerts.provider.TranslationProvider;
import org.myalerts.view.TestScenarioDetailedView;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class TestScenarioGrid extends Composite<VerticalLayout> {

    private final PaginatedGrid<TestScenario> paginatedGrid = new PaginatedGrid<>();

    private final Binder<TestScenario> testScenarioBinder = new Binder<>(TestScenario.class);

    private final TestScenarioEventHandler eventHandler;

    private final TranslationProvider translationProvider;

    private final boolean isLoggedAsAdmin = UserRole.ADMIN.validate();

    private final boolean isLogged = UserRole.LOGGED.validate();

    public void refreshPage() {
        paginatedGrid.refreshPaginator();
    }

    public void setDataProvider(final DataProvider<TestScenario, ?> dataProvider) {
        paginatedGrid.setDataProvider(dataProvider);
    }

    @Override
    protected VerticalLayout initContent() {
        final var layout = super.initContent();

        layout.setSizeFull();
        paginatedGrid.setHeightByRows(true);
        paginatedGrid.addColumn(new ComponentRenderer<>(this::renderIsEnabled))
            .setAutoWidth(true);
        paginatedGrid.addColumn(new ComponentRenderer<>(this::renderName))
            .setHeader(getTranslation("test-scenario.main-grid.name.column"))
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
        layout.add(paginatedGrid);

        return layout;
    }

    @RequiresUIThread
    private Component renderIsEnabled(final TestScenario testScenario) {
        final var toggleButton = new ToggleButton(testScenario.isEnabled());
        toggleButton.addValueChangeListener(event -> eventHandler.onActivationChanged(testScenario));
        return toggleButton;
    }

    @RequiresUIThread
    private Component renderName(final TestScenario testScenario) {
        if (!testScenario.isEditable()) {
            final var nameAnchor = new Anchor(RouteConfiguration.forApplicationScope()
                .getUrl(TestScenarioDetailedView.class, new RouteParameters(TestScenarioDetailedView.ID_PARAM, String.valueOf(testScenario.getId()))),
                StringUtils.abbreviate(testScenario.getName(), 64));
            nameAnchor.addClassName(getClassName(testScenario));
            return nameAnchor;
        }

        final var textField = new TextField();
        testScenarioBinder.forField(textField)
            .withValidator(name -> true, StringUtils.EMPTY)
            .bind(TestScenario::getName, (Setter<TestScenario, String>) eventHandler::onNameChanged);
        testScenarioBinder.setBean(testScenario);

        textField.addKeyUpListener(Key.ENTER, event -> onCronExpressionUpdated(testScenario));
        textField.addKeyUpListener(Key.ESCAPE, event -> onCronExpressionCancelled(testScenario));

        return textField;
    }

    @RequiresUIThread
    private Component renderLastRun(final TestScenario testScenario) {
        final var lastRunButton = new Button(Optional.ofNullable(testScenario.getLastRunTime())
            .map(translationProvider::prettyTimeFormat)
            .orElseGet(() -> getTranslation("test-scenario.main-grid.not-available")));
        lastRunButton.addClickListener(event -> new TestScenarioHistoryDialog(() -> eventHandler.getLastResults(testScenario)).open());
        return lastRunButton;
    }

    @RequiresUIThread
    private Component renderCronExpression(final TestScenario testScenario) {
        if (!testScenario.isEditable()) {
            return new Label(testScenario.getCron());
        }

        final var textField = new TextField();
        testScenarioBinder.forField(textField)
            .withValidator(cron -> true, StringUtils.EMPTY)
            .bind(TestScenario::getCron, (Setter<TestScenario, String>) eventHandler::onCronExpressionChanged);
        testScenarioBinder.setBean(testScenario);

        textField.addKeyUpListener(Key.ENTER, event -> onCronExpressionUpdated(testScenario));
        textField.addKeyUpListener(Key.ESCAPE, event -> onCronExpressionCancelled(testScenario));

        return textField;
    }

    @RequiresUIThread
    private Component renderActions(final TestScenario testScenario) {
        final var layout = new HorizontalLayout();

        final var scheduleNowButton = new Button(VaadinIcon.START_COG.create());
        scheduleNowButton.getElement().setProperty("title", getTranslation("test-scenario.main-grid.actions.button.schedule.title"));
        final var editButton = new Button(VaadinIcon.EDIT.create());
        editButton.getElement().setProperty("title", getTranslation("test-scenario.main-grid.actions.button.edit.title"));
        final var deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.getElement().setProperty("title", getTranslation("test-scenario.main-grid.actions.button.delete.title"));

        if (isLogged) {
            scheduleNowButton.addClickListener(event -> onScheduleNow(testScenario));
        } else {
            scheduleNowButton.setEnabled(false);
        }

        if (isLoggedAsAdmin) {
            editButton.addClickListener(event -> onCronExpressionToEdit(testScenario));
            if (!testScenario.isEnabled()) {
                deleteButton.addClickListener(event -> eventHandler.onDelete(testScenario));
            } else {
                deleteButton.setEnabled(false);
            }
        } else {
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }

        layout.add(scheduleNowButton, editButton, deleteButton);
        return layout;
    }

    private void onCronExpressionToEdit(final TestScenario testScenario) {
        testScenario.setEditable(true);
        paginatedGrid.getDataProvider().refreshItem(testScenario);
    }

    private void onCronExpressionUpdated(final TestScenario testScenario) {
        paginatedGrid.getDataProvider().refreshItem(testScenario);
    }

    private void onCronExpressionCancelled(final TestScenario testScenario) {
        testScenario.setEditable(false);
        paginatedGrid.getDataProvider().refreshItem(testScenario);
    }

    private void onScheduleNow(final TestScenario testScenario) {
        eventHandler.onScheduleNow(testScenario);
        paginatedGrid.getDataProvider().refreshItem(testScenario);
    }

    private String getClassName(final TestScenario testScenario) {
        if (!testScenario.isEnabled()) {
            return TestScenarioType.DISABLED.getLabel();
        }

        return testScenario.isFailed() ? TestScenarioType.FAILED.getLabel() : TestScenarioType.PASSED.getLabel();
    }

}
