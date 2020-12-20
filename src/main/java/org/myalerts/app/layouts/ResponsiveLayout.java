package org.myalerts.app.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public abstract class ResponsiveLayout extends FlexLayout {

    public ResponsiveLayout() {
        setId("responsiveLayout");
    }

    protected Component createHeader(String subTitleOnLeft, Component... componentsOnRight) {
        Div headerDiv = new Div();
        headerDiv.addClassName("header");

        Div leftHeaderDiv = new Div();
        leftHeaderDiv.addClassName("header-left");
        H3 pageSubTitle = new H3(subTitleOnLeft);
        leftHeaderDiv.add(pageSubTitle);
        headerDiv.add(leftHeaderDiv);

        Div rightHeaderDiv = new Div();
        rightHeaderDiv.addClassName("header-right");
        rightHeaderDiv.add(componentsOnRight);
        headerDiv.add(rightHeaderDiv);

        return headerDiv;
    }

    protected Component createContent(Component... components) {
        Div contentDiv = new Div();
        contentDiv.addClassName("content");
        contentDiv.add(components);
        return contentDiv;
    }

    protected Component createFooter() {
        Div footerDiv = new Div();
        footerDiv.addClassName("footer");
        footerDiv.add(new Html(getTranslation("footer.copyright")));
        return footerDiv;
    }

}
