package org.myalerts.view.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public abstract class ResponsiveLayout extends FlexLayout {

    public ResponsiveLayout() {
        setId("responsiveLayout");
    }

    protected Component createHeader(String subTitleOnLeft, Component... componentsOnRight) {
        final var headerDiv = new Div();
        headerDiv.addClassName("header");

        final var leftHeaderDiv = new Div();
        leftHeaderDiv.addClassName("header-left");
        final var pageSubTitle = new H3(subTitleOnLeft);
        leftHeaderDiv.add(pageSubTitle);
        headerDiv.add(leftHeaderDiv);

        final var rightHeaderDiv = new Div();
        rightHeaderDiv.addClassName("header-right");
        rightHeaderDiv.add(componentsOnRight);
        headerDiv.add(rightHeaderDiv);

        return headerDiv;
    }

    protected Component createContent(Component... components) {
        final var contentDiv = new Div();
        contentDiv.addClassName("content");
        contentDiv.add(components);
        return contentDiv;
    }

    protected Component createFooter() {
        final var footerDiv = new Div();
        footerDiv.addClassName("footer");
        footerDiv.add(new Html(getTranslation("footer.copyright")));
        return footerDiv;
    }

}
