package org.myalerts.app.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.app.components.TestScenarioGrid;
import org.myalerts.app.events.TestScenarioEventHandler;
import org.myalerts.app.layouts.BaseLayout;
import org.myalerts.app.layouts.ResponsiveLayout;
import org.myalerts.app.models.TestScenario;
import org.myalerts.app.models.TestScenarioFilter;
import org.myalerts.app.models.TestScenarioType;
import org.myalerts.app.services.TestScenarioService;

import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

@Slf4j
@Route(value = TestScenarioView.ROUTE, layout = BaseLayout.class)
public class TestScenarioView extends ResponsiveLayout implements TestScenarioEventHandler {

    public static final String ROUTE = "test-scenarios";

    private final TestScenarioGrid testScenarioGrid;

    private final TestScenarioFilter testScenarioFilter = new TestScenarioFilter();

    private final TestScenarioService testScenarioService;

    public TestScenarioView(TestScenarioService testScenarioService) {
        super();
        this.testScenarioService = testScenarioService;

        ConfigurableFilterDataProvider<TestScenario, Void, TestScenarioFilter> configurableFilterDataProvider = DataProvider
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
    public void onActivationChanged(TestScenario testScenario) {
        testScenarioService.changeActivation(testScenario);
        testScenarioGrid.refreshPage();

        Notification.show("Activation status for test scenario " + testScenario.getName() +
                " was changed successfully.");
    }

    @Override
    public void onCronExpressionChanged(@NotNull TestScenario testScenario, @NotNull String newCronExpression) {
        testScenarioService.changeCronExpression(testScenario, newCronExpression);

        Notification.show("Cron expression for test scenario " + testScenario.getName() +
                " was changed successfully to " + newCronExpression + ".");
    }

    private Component createFilterByName() {
        TextField filterByNameTextField = new TextField();
        filterByNameTextField.setPlaceholder(getTranslation("test-scenario.main-grid.filter.by-name.placeholder"));
        filterByNameTextField.setClearButtonVisible(true);
        filterByNameTextField.setValueChangeMode(ValueChangeMode.LAZY);
        filterByNameTextField.setValueChangeTimeout((int) TimeUnit.SECONDS.toMillis(1));
        filterByNameTextField.addValueChangeListener(event -> onFilteringByName(event.getValue()));
        return filterByNameTextField;
    }

    private Component createFilterByType() {
        ComboBox<TestScenarioType> filterByTypeComboBox = new ComboBox<>();
        filterByTypeComboBox.setItems(TestScenarioType::findByQuery);
        filterByTypeComboBox.setItemLabelGenerator(TestScenarioType::getLabel);
        filterByTypeComboBox.addValueChangeListener(event -> onFilteringByType(event.getValue()));
        return filterByTypeComboBox;
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
