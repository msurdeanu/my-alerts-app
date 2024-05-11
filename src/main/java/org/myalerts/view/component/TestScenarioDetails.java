package org.myalerts.view.component;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.f0rce.ace.AceEditor;
import de.f0rce.ace.enums.AceMode;
import lombok.RequiredArgsConstructor;
import org.myalerts.domain.TestScenario;
import org.myalerts.domain.TestScenarioEventHandler;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RequiredArgsConstructor
public final class TestScenarioDetails extends Composite<VerticalLayout> {

    private final AceEditor editor = new AceEditor();

    private final Button saveButton = new Button(getTranslation("test-scenario.main-grid.button.save"));

    private final TestScenarioEventHandler testScenarioEventHandler;

    @Override
    protected VerticalLayout initContent() {
        final var layout = super.initContent();
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        editor.addClassName("ace-editor");
        editor.setMode(AceMode.groovy);
        layout.add(editor);

        saveButton.setEnabled(false);
        saveButton.setWidthFull();
        saveButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        layout.add(saveButton);

        return layout;
    }

    public void setDetails(final TestScenario testScenario) {
        editor.setValue(testScenario.getDefinition().getScript());
        editor.addAceChangedListener(event -> saveButton.setEnabled(isNewDefinition(testScenario, event.getValue())));

        saveButton.addClickListener(event -> testScenarioEventHandler.onDefinitionChanged(testScenario, editor.getValue()));
    }

    private boolean isNewDefinition(final TestScenario testScenario,
                                    final String newDefinition) {
        return !testScenario.getDefinition().getScript().equals(newDefinition);
    }

}
