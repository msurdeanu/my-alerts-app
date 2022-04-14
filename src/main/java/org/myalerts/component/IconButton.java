package org.myalerts.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public class IconButton extends Button {

    public IconButton(final Icon icon) {
        super(icon);
        init();
    }

    private void init() {
        addClassName("app-layout-icon-button");
        setWidth("var(--app-layout-menu-button-height)");
        setHeight("var(--app-layout-menu-button-height)");
        addThemeNames(ButtonVariant.LUMO_TERTIARY.getVariantName(), ButtonVariant.LUMO_ICON.getVariantName(), ButtonVariant.LUMO_LARGE.getVariantName());
    }

}
