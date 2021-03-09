package org.myalerts.app.view;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Route(value = IndexView.ROUTE)
public class IndexView extends Composite<Div> implements BeforeEnterObserver {

    public static final String ROUTE = StringUtils.EMPTY;

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        beforeEnterEvent.rerouteTo(TestScenarioView.class);
    }

}
