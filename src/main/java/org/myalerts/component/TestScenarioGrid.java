package org.myalerts.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
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
import de.f0rce.ace.AceEditor;
import de.f0rce.ace.enums.AceMode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.myalerts.domain.TestScenario;
import org.myalerts.domain.TestScenarioType;
import org.myalerts.domain.UserRole;
import org.myalerts.domain.event.TestScenarioEventHandler;
import org.myalerts.marker.RequiresUIThread;
import org.myalerts.provider.TranslationProvider;
import org.myalerts.view.TestScenarioDetailedView;
import org.vaadin.klaudeta.PaginatedGrid;

import java.util.Optional;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class TestScenarioGrid extends Composite<VerticalLayout> {

    private final PaginatedGrid<TestScenario> paginatedGrid = new PaginatedGrid<>();

    private final Binder<TestScenario> testScenarioBinder = new Binder<>(TestScenario.class);

    private final TestScenarioEventHandler testScenarioEventHandler;

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
        paginatedGrid.setItemDetailsRenderer(new ComponentRenderer<>(
            () -> new TestScenarioDetails(testScenarioEventHandler),
            TestScenarioDetails::setDetails)
        );
        paginatedGrid.setPageSize(10);
        paginatedGrid.setPaginatorSize(5);
        paginatedGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        layout.add(paginatedGrid);

        return layout;
    }

    @RequiresUIThread
    private Component renderIsEnabled(final TestScenario testScenario) {
        final var toggleButton = new Checkbox(testScenario.isEnabled());
        toggleButton.addValueChangeListener(event -> testScenarioEventHandler.onActivationChanged(testScenario));
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
        textField.addClassName("editable-field");
        testScenarioBinder.forField(textField)
            .withValidator(name -> true, StringUtils.EMPTY)
            .bind(TestScenario::getName, (Setter<TestScenario, String>) testScenarioEventHandler::onNameChanged);
        testScenarioBinder.readBean(testScenario);
        setSuffixForField(textField);

        textField.addKeyDownListener(Key.ENTER, event -> onCronExpressionUpdated(testScenario));
        textField.addKeyDownListener(Key.ESCAPE, event -> onCronExpressionCancelled(testScenario));

        return textField;
    }

    @RequiresUIThread
    private Component renderLastRun(final TestScenario testScenario) {
        final var lastRunButton = new Button(Optional.ofNullable(testScenario.getLastRunTime())
            .map(translationProvider::prettyTimeFormat)
            .orElseGet(() -> getTranslation("test-scenario.main-grid.not-available")));
        lastRunButton.addClickListener(event -> new TestScenarioHistoryDialog(() -> testScenarioEventHandler.getLastResults(testScenario)).open());
        lastRunButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        return lastRunButton;
    }

    @RequiresUIThread
    private Component renderCronExpression(final TestScenario testScenario) {
        if (!testScenario.isEditable()) {
            return new Label(testScenario.getCron());
        }

        final var textField = new TextField();
        textField.addClassName("editable-field");
        testScenarioBinder.forField(textField)
            .withValidator(cron -> true, StringUtils.EMPTY)
            .bind(TestScenario::getCron, (Setter<TestScenario, String>) testScenarioEventHandler::onCronExpressionChanged);
        testScenarioBinder.readBean(testScenario);
        setSuffixForField(textField);

        textField.addKeyDownListener(Key.ENTER, event -> onCronExpressionUpdated(testScenario));
        textField.addKeyDownListener(Key.ESCAPE, event -> onCronExpressionCancelled(testScenario));

        return textField;
    }

    @RequiresUIThread
    private Component renderActions(final TestScenario testScenario) {
        final var layout = new HorizontalLayout();

        final var scheduleNowButton = new Button(new Icon(VaadinIcon.START_COG));
        scheduleNowButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        scheduleNowButton.getElement().setProperty("title", getTranslation("test-scenario.main-grid.actions.button.schedule.title"));
        scheduleNowButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        final var editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.getElement().setProperty("title", getTranslation("test-scenario.main-grid.actions.button.edit.title"));
        editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        final var deleteButton = new Button();
        deleteButton.setIcon(new Icon(VaadinIcon.TRASH));
        deleteButton.getElement().setProperty("title", getTranslation("test-scenario.main-grid.actions.button.delete.title"));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        if (isLogged) {
            scheduleNowButton.addClickListener(event -> onScheduleNow(testScenario));
        } else {
            scheduleNowButton.setEnabled(false);
        }

        if (isLoggedAsAdmin) {
            editButton.addClickListener(event -> onCronExpressionToEdit(testScenario));
            if (!testScenario.isEnabled()) {
                deleteButton.addClickListener(event -> testScenarioEventHandler.onDelete(testScenario));
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
        testScenario.toggleOnEditing();
        paginatedGrid.getDataProvider().refreshItem(testScenario);
    }

    private void onCronExpressionUpdated(final TestScenario testScenario) {
        testScenario.setEditable(false);
        testScenarioBinder.writeBeanIfValid(testScenario);
        paginatedGrid.getDataProvider().refreshItem(testScenario);
    }

    private void onCronExpressionCancelled(final TestScenario testScenario) {
        testScenario.setEditable(false);
        paginatedGrid.getDataProvider().refreshItem(testScenario);
    }

    private void onScheduleNow(final TestScenario testScenario) {
        testScenarioEventHandler.onScheduleNow(testScenario);
        paginatedGrid.getDataProvider().refreshItem(testScenario);
    }

    private String getClassName(final TestScenario testScenario) {
        if (!testScenario.isEnabled()) {
            return TestScenarioType.DISABLED.getLabel();
        }

        return testScenario.isFailed() ? TestScenarioType.FAILED.getLabel() : TestScenarioType.PASSED.getLabel();
    }

    private void setSuffixForField(final TextField textField) {
        final var span = new Span(getTranslation("test-scenario.main-grid.save-with-enter"));
        span.addClassName("small-suffix");

        ComponentEnricher.setComponentAsSuffix(textField, span);
    }

    private static class TestScenarioDetails extends VerticalLayout {

        private final AceEditor editor = new AceEditor();

        private final Button saveButton = new Button(getTranslation("test-scenario.detailed.button.save"));

        private final TestScenarioEventHandler testScenarioEventHandler;

        public TestScenarioDetails(final TestScenarioEventHandler testScenarioEventHandler) {
            this.testScenarioEventHandler = testScenarioEventHandler;

            setDefaultHorizontalComponentAlignment(Alignment.CENTER);

            editor.addClassName("ace-editor");
            editor.setMode(AceMode.groovy);
            editor.setAutoComplete(true);
            editor.setLiveAutocompletion(true);
            add(editor);

            saveButton.setEnabled(false);
            saveButton.setWidthFull();
            saveButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            add(saveButton);
        }

        public void setDetails(final TestScenario testScenario) {
            editor.setValue(testScenario.getDefinition().getScript());
            editor.addAceChangedListener(event -> saveButton.setEnabled(isNewDefinition(testScenario, event.getValue())));

            saveButton.addClickListener(event -> testScenarioEventHandler.onDefinitionChanged(testScenario, editor.getValue()));
        }

        private boolean isNewDefinition(final TestScenario testScenario, final String newDefinition) {
            return !testScenario.getDefinition().getScript().equals(newDefinition);
        }

    }

}
