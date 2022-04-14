package org.myalerts.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.f0rce.ace.AceEditor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.layout.BaseLayout;
import org.myalerts.layout.ResponsiveLayout;
import org.myalerts.marker.RequiresUIThread;
import org.myalerts.domain.TestScenario;
import org.myalerts.domain.UserRole;
import org.myalerts.service.TestScenarioService;

import static org.myalerts.view.TestScenarioDetailedView.ID_PARAM;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@AnonymousAllowed
@RequiredArgsConstructor
@Route(value = "test-scenarios/:" + ID_PARAM + "/detailed", layout = BaseLayout.class)
public class TestScenarioDetailedView extends ResponsiveLayout implements BeforeEnterObserver {

    public static final String ID_PARAM = "id";

    private final TestScenarioService testScenarioService;

    private final boolean isLoggedAsAdmin = UserRole.ADMIN.validate();

    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        testScenarioService.findBy(beforeEnterEvent.getRouteParameters().getInteger(ID_PARAM).orElse(0))
                .ifPresent(this::createLayout);
    }

    private void createLayout(final TestScenario testScenario) {
        add(createHeader(testScenario.getName()));
        add(createContent(createSplit(testScenario)));
        add(createFooter());
    }

    private Component createSplit(final TestScenario testScenario) {
        final var layout = new SplitLayout(new Label(), createRightSide(testScenario));
        layout.setSplitterPosition(50);
        return layout;
    }

    private Component createRightSide(final TestScenario testScenario) {
        final var layout = new VerticalLayout();
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        if (isLoggedAsAdmin) {
            final var saveButton = new Button(getTranslation("test-scenario.detailed.button.save"));
            saveButton.setEnabled(false);
            final var editor = new AceEditor();
            editor.addClassName("ace-editor");
            editor.setAutoComplete(true);
            editor.setLiveAutocompletion(true);
            editor.setValue(testScenario.getDefinition().getScript());
            editor.addAceChangedListener(event -> saveButton.setEnabled(isNewDefinition(testScenario, event.getValue())));
            saveButton.addClickListener(event -> applyChangesToDefinition(testScenario, editor.getValue()));
            layout.add(new H4(getTranslation("test-scenario.detailed.definition.title")), editor, saveButton);
        }
        return layout;
    }

    @RequiresUIThread
    private void applyChangesToDefinition(final TestScenario testScenario, final String newDefinition) {
        testScenarioService.changeDefinition(testScenario, newDefinition);
    }

    private boolean isNewDefinition(final TestScenario testScenario, final String newDefinition) {
        return !testScenario.getDefinition().getScript().equals(newDefinition);
    }

}
