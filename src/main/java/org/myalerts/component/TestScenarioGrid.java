package org.myalerts.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.RequiredArgsConstructor;
import org.myalerts.domain.TestScenario;
import org.myalerts.domain.TestScenarioEventHandler;
import org.myalerts.domain.TestScenarioType;
import org.myalerts.domain.UserRole;
import org.myalerts.provider.TranslationProvider;
import org.vaadin.klaudeta.PaginatedGrid;

import java.util.Set;

import static com.vaadin.flow.component.Shortcuts.addShortcutListener;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.abbreviate;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RequiredArgsConstructor
public final class TestScenarioGrid extends Composite<VerticalLayout> {

    private final PaginatedGrid<TestScenario, ?> paginatedGrid = new PaginatedGrid<>();

    private final TestScenarioEventHandler testScenarioEventHandler;

    private final Set<String> allTags;

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
        paginatedGrid.setAllRowsVisible(true);
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

    private Component renderIsEnabled(final TestScenario testScenario) {
        final var toggleButton = new Checkbox(testScenario.isEnabled());
        toggleButton.addValueChangeListener(event -> testScenarioEventHandler.onActivationChanged(testScenario));
        return toggleButton;
    }

    private Component renderName(final TestScenario testScenario) {
        final var layout = new HorizontalLayout();
        if (!testScenario.isEditable()) {
            testScenario.getTags().stream().map(tag -> {
                final var span = new Span(tag.getName());
                span.getElement().getThemeList().add("badge");
                return span;
            }).forEach(layout::add);

            final var name = new NativeLabel(abbreviate(testScenario.getName(), 64));
            name.addClassName(getClassName(testScenario));
            layout.add(name);
            return layout;
        }

        final var tagsComboBox = new MultiSelectComboBox<String>();
        tagsComboBox.addClassName("editable-field");
        tagsComboBox.setItems(allTags);
        tagsComboBox.setValue(testScenario.getTagsAsString());
        tagsComboBox.setAllowCustomValue(true);
        tagsComboBox.addCustomValueSetListener(event -> {
            final var customValue = event.getDetail();
            allTags.add(customValue);
            tagsComboBox.setItems(allTags);
        });
        layout.add(tagsComboBox);

        final var nameTextField = new TextField();
        nameTextField.addClassName("editable-field");
        nameTextField.setSuffixComponent(VaadinIcon.ENTER.create());
        nameTextField.setValue(testScenario.getName());
        layout.add(nameTextField);

        addShortcutListener(layout, () -> {
            testScenarioEventHandler.onTagsChanged(testScenario, tagsComboBox.getValue());
            testScenarioEventHandler.onNameChanged(testScenario, nameTextField.getValue());
            onTestScenarioUpdated(testScenario);
        }, Key.ENTER);
        addShortcutListener(layout, () -> onTestScenarioCancelled(testScenario), Key.ESCAPE);
        return layout;
    }

    private Component renderLastRun(final TestScenario testScenario) {
        final var lastRunButton = new Button(ofNullable(testScenario.getLastRunTime())
            .map(lastRun -> getTranslation(TranslationProvider.PRETTY_TIME_FORMAT, lastRun))
            .orElseGet(() -> getTranslation("test-scenario.main-grid.not-available")));
        lastRunButton.addClickListener(event -> new TestScenarioHistoryDialog(() -> testScenarioEventHandler.getLastResults(testScenario)).open());
        lastRunButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        return lastRunButton;
    }

    private Component renderCronExpression(final TestScenario testScenario) {
        if (!testScenario.isEditable()) {
            return new NativeLabel(testScenario.getCron());
        }

        final var textField = new TextField();
        textField.addClassName("editable-field");
        textField.setSuffixComponent(VaadinIcon.ENTER.create());
        textField.setValue(testScenario.getCron());

        addShortcutListener(textField, () -> {
            testScenarioEventHandler.onCronExpressionChanged(testScenario, textField.getValue());
            onTestScenarioUpdated(testScenario);
        }, Key.ENTER);
        addShortcutListener(textField, () -> onTestScenarioCancelled(testScenario), Key.ESCAPE);
        return textField;
    }

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
            editButton.addClickListener(event -> onTestScenarioToEdit(testScenario));
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

    private void onTestScenarioToEdit(final TestScenario testScenario) {
        testScenario.toggleOnEditing();
        paginatedGrid.getDataProvider().refreshItem(testScenario);
    }

    private void onTestScenarioUpdated(final TestScenario testScenario) {
        testScenario.setEditable(false);
        paginatedGrid.getDataProvider().refreshItem(testScenario);
    }

    private void onTestScenarioCancelled(final TestScenario testScenario) {
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


}
