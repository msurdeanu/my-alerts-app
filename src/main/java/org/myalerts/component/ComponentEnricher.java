package org.myalerts.component;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.dom.Element;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public final class ComponentEnricher {

    public static Component getComponentAsSuffix(final Component target) {
        return getChildInSlot(target, "prefix");
    }

    public static void setComponentAsSuffix(final Component target, final Component component) {
        clearSlot(target, "suffix");

        if (component != null) {
            component.getElement().setAttribute("slot", "suffix");
            target.getElement().appendChild(component.getElement());
        }
    }

    private static Stream<Element> getElementsInSlot(final HasElement target, final String slot) {
        return target.getElement().getChildren().filter(child -> slot.equals(child.getAttribute("slot")));
    }

    private static void clearSlot(final Component target, final String slot) {
        getElementsInSlot(target, slot).collect(Collectors.toList()).forEach(target.getElement()::removeChild);
    }

    private static Component getChildInSlot(final HasElement target, final String slot) {
        Optional<Element> element = getElementsInSlot(target, slot).findFirst();
        return element.map(value -> value.getComponent().get()).orElse(null);
    }

}
