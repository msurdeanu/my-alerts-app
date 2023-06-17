package org.myalerts.view;

import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.component.TestScenarioGrid;
import org.myalerts.domain.TestScenario;
import org.myalerts.domain.TestScenarioEventHandler;
import org.myalerts.domain.TestScenarioFilter;
import org.myalerts.domain.TestScenarioResult;
import org.myalerts.domain.TestScenarioType;
import org.myalerts.layout.BaseLayout;
import org.myalerts.layout.ResponsiveLayout;
import org.myalerts.service.TestScenarioService;
import org.myalerts.service.event.TestScenarioRunEventService;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@AnonymousAllowed
@Route(value = TestScenarioView.ROUTE, layout = BaseLayout.class)
public class TestScenarioView extends ResponsiveLayout implements HasDynamicTitle, HasUrlParameter<String>, TestScenarioEventHandler {

    public static final String ROUTE = "test-scenarios";

    private final TestScenarioFilter testScenarioFilter = new TestScenarioFilter();

    private final Select<TestScenarioType> filterByType;

    private final TestScenarioGrid testScenarioGrid;

    private final TestScenarioService testScenarioService;

    private final TestScenarioRunEventService testScenarioResultService;

    public TestScenarioView(final TestScenarioService testScenarioService,
                            final TestScenarioRunEventService testScenarioResultService) {
        super();
        this.testScenarioService = testScenarioService;
        this.testScenarioResultService = testScenarioResultService;

        final ConfigurableFilterDataProvider<TestScenario, Void, TestScenarioFilter> configurableFilterDataProvider = DataProvider
                .fromFilteringCallbacks(testScenarioService::findBy, testScenarioService::countBy)
                .withConfigurableFilter();
        configurableFilterDataProvider.setFilter(testScenarioFilter);

        testScenarioGrid = new TestScenarioGrid(this, testScenarioService.getAllTags());
        testScenarioGrid.setDataProvider(configurableFilterDataProvider);

        filterByType = createFilterByType();

        add(createHeader(getTranslation("test-scenario.page.subtitle"), filterByType,
                createFilterByTag(testScenarioService),
                createFilterByName()));
        add(createContent(testScenarioGrid));
        add(createFooter());
    }

    @Override
    public String getPageTitle() {
        return getTranslation("site.base.title", getTranslation("menu.main.test-scenarios"));
    }

    @Override
    public void setParameter(final BeforeEvent event, final @OptionalParameter String parameter) {
        if (parameter != null) {
            filterByType.setValue(TestScenarioType.of(parameter));
        }
    }

    @Override
    public Collection<TestScenarioResult> getLastResults(final TestScenario testScenario) {
        return testScenarioResultService.getLastResults(testScenario.getId());
    }

    @Override
    public void onActivationChanged(final TestScenario testScenario) {
        testScenarioService.changeActivation(testScenario);
        testScenarioGrid.refreshPage();

        Notification.show("Activation status for test scenario '" + testScenario.getName() + "' is changed successfully.");
    }

    @Override
    public void onCronExpressionChanged(final TestScenario testScenario, @NonNull final String newCronExpression) {
        if (testScenarioService.changeCronExpression(testScenario, newCronExpression)) {
            Notification.show("Cron expression changed successfully to '" + newCronExpression + "'.");
        }
    }

    @Override
    public void onDefinitionChanged(final TestScenario testScenario, final String newDefinition) {
        if (testScenarioService.changeDefinition(testScenario, newDefinition)) {
            Notification.show("Test scenario definition changed successfully.");
        }
    }

    @Override
    public void onDelete(final TestScenario testScenario) {
        testScenarioService.delete(testScenario);
        testScenarioGrid.refreshPage();
    }

    @Override
    public void onNameChanged(final TestScenario testScenario, @NonNull final String newName) {
        if (testScenarioService.changeName(testScenario, newName)) {
            Notification.show("Test scenario name changed successfully to '" + newName + "'.");
        }
    }

    @Override
    public void onScheduleNow(final TestScenario testScenario) {
        try {
            testScenarioService.scheduleNowInSyncMode(testScenario);
        } catch (Exception e) {
            log.error("An error occurred during scheduling immediately a test scenario.", e);
            Notification.show("An error occurred during scheduling immediately a test scenario. More details about exception are present in the logs.");
        }
    }

    @Override
    public void onTagsChanged(final TestScenario testScenario, final Set<String> newTags) {
        if (testScenarioService.changeTags(testScenario, newTags)) {
            Notification.show("Test scenario tags changed successfully to '" + newTags + "'.");
        }
    }

    private Select<TestScenarioType> createFilterByType() {
        final var filterByTypeSelect = new Select<TestScenarioType>();
        filterByTypeSelect.setItems(TestScenarioType.getAllItems());
        filterByTypeSelect.setPlaceholder(getTranslation("test-scenario.main-grid.filter.by-type.placeholder"));
        filterByTypeSelect.setHelperText(getTranslation("test-scenario.main-grid.filter.by-type.helper"));
        filterByTypeSelect.setItemLabelGenerator(TestScenarioType::getLabel);
        filterByTypeSelect.setValue(TestScenarioType.ALL);
        filterByTypeSelect.addValueChangeListener(event -> onFilteringByType(event.getValue()));
        return filterByTypeSelect;
    }

    private MultiSelectComboBox<String> createFilterByTag(final TestScenarioService testScenarioService) {
        final var filterByTagComboBox = new MultiSelectComboBox<String>();
        filterByTagComboBox.setPlaceholder(getTranslation("test-scenario.main-grid.filter.by-tag.placeholder"));
        filterByTagComboBox.setHelperText(getTranslation("test-scenario.main-grid.filter.by-tag.helper"));
        filterByTagComboBox.setItems(testScenarioService.getAllTags());
        filterByTagComboBox.addValueChangeListener(event -> onFilteringByTag(event.getValue()));
        return filterByTagComboBox;
    }

    private TextField createFilterByName() {
        final var filterByNameTextField = new TextField();
        filterByNameTextField.setPlaceholder(getTranslation("test-scenario.main-grid.filter.by-name.placeholder"));
        filterByNameTextField.setHelperText(getTranslation("test-scenario.main-grid.filter.by-name.helper"));
        filterByNameTextField.setClearButtonVisible(true);
        filterByNameTextField.setValueChangeMode(ValueChangeMode.LAZY);
        filterByNameTextField.setValueChangeTimeout((int) TimeUnit.SECONDS.toMillis(1));
        filterByNameTextField.addValueChangeListener(event -> onFilteringByName(event.getValue()));
        return filterByNameTextField;
    }

    private void onFilteringByType(final TestScenarioType value) {
        testScenarioFilter.setByTypeCriteria(value);

        testScenarioGrid.refreshPage();
    }

    private void onFilteringByTag(final Set<String> tags) {
        testScenarioFilter.setByTagCriteria(tags);

        testScenarioGrid.refreshPage();
    }

    private void onFilteringByName(final String value) {
        testScenarioFilter.setByNameCriteria(value.toLowerCase());

        testScenarioGrid.refreshPage();
    }

}
