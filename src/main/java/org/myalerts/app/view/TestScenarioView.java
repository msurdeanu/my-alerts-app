package org.myalerts.app.view;

import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;

import org.myalerts.app.component.TestScenarioGrid;
import org.myalerts.app.event.TestScenarioEventHandler;
import org.myalerts.app.layout.BaseLayout;
import org.myalerts.app.layout.ResponsiveLayout;
import org.myalerts.app.model.TestScenario;
import org.myalerts.app.model.TestScenarioFilter;
import org.myalerts.app.model.TestScenarioType;
import org.myalerts.app.service.TestScenarioService;

@Slf4j
@AnonymousAllowed
@Route(value = TestScenarioView.ROUTE, layout = BaseLayout.class)
public class TestScenarioView extends ResponsiveLayout implements HasDynamicTitle, TestScenarioEventHandler {

    public static final String ROUTE = "test-scenarios";

    private final TestScenarioGrid testScenarioGrid;

    private final TestScenarioFilter testScenarioFilter = new TestScenarioFilter();

    private final TestScenarioService testScenarioService;

    public TestScenarioView(TestScenarioService testScenarioService) {
        super();
        this.testScenarioService = testScenarioService;

        final ConfigurableFilterDataProvider<TestScenario, Void, TestScenarioFilter> configurableFilterDataProvider = DataProvider
            .fromFilteringCallbacks(testScenarioService::findBy, testScenarioService::countBy)
            .withConfigurableFilter();
        configurableFilterDataProvider.setFilter(testScenarioFilter);

        testScenarioGrid = new TestScenarioGrid(this);
        testScenarioGrid.setDataProvider(configurableFilterDataProvider);

        add(createHeader(getTranslation("test-scenario.page.subtitle"), createFilterByName(), createFilterByType()));
        add(createContent(testScenarioGrid));
        add(createFooter());
    }

    @Override
    public String getPageTitle() {
        return getTranslation("site.base.title", getTranslation("menu.main.test-scenarios"));
    }

    @Override
    public void onActivationChanged(TestScenario testScenario) {
        testScenarioService.changeActivation(testScenario);
        testScenarioGrid.refreshPage();

        Notification.show("Activation status for test scenario '" + testScenario.getName() + "' is changed successfully.");
    }

    @Override
    public void onCronExpressionChanged(@NotNull TestScenario testScenario, @NotNull String newCronExpression) {
        testScenarioService.changeCronExpression(testScenario, newCronExpression);

        Notification.show("Cron expression for test scenario '" + testScenario.getName() + "' changed successfully to '" + newCronExpression + "'.");
    }

    private Component createFilterByName() {
        final TextField filterByNameTextField = new TextField();
        filterByNameTextField.setPlaceholder(getTranslation("test-scenario.main-grid.filter.by-name.placeholder"));
        filterByNameTextField.setHelperText(getTranslation("test-scenario.main-grid.filter.by-name.helper"));
        filterByNameTextField.setClearButtonVisible(true);
        filterByNameTextField.setValueChangeMode(ValueChangeMode.LAZY);
        filterByNameTextField.setValueChangeTimeout((int) TimeUnit.SECONDS.toMillis(1));
        filterByNameTextField.addValueChangeListener(event -> onFilteringByName(event.getValue()));
        return filterByNameTextField;
    }

    private Component createFilterByType() {
        final Select<TestScenarioType> filterByTypeSelect = new Select<>();
        filterByTypeSelect.setItems(TestScenarioType.getAllItems());
        filterByTypeSelect.setPlaceholder(getTranslation("test-scenario.main-grid.filter.by-type.placeholder"));
        filterByTypeSelect.setHelperText(getTranslation("test-scenario.main-grid.filter.by-type.helper"));
        filterByTypeSelect.setItemLabelGenerator(TestScenarioType::getLabel);
        filterByTypeSelect.setValue(TestScenarioType.ALL);
        filterByTypeSelect.addValueChangeListener(event -> onFilteringByType(event.getValue()));
        return filterByTypeSelect;
    }

    private void onFilteringByName(String value) {
        testScenarioFilter.setByNameCriteria(value.toLowerCase());

        testScenarioGrid.refreshPage();
    }

    private void onFilteringByType(TestScenarioType value) {
        testScenarioFilter.setByTypeCriteria(value);

        testScenarioGrid.refreshPage();
    }

}
