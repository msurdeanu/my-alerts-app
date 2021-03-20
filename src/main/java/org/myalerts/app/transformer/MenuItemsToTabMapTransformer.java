package org.myalerts.app.transformer;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.RouterLink;
import de.codecamp.vaadin.security.spring.access.VaadinSecurity;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import org.myalerts.app.model.MenuItem;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public class MenuItemsToTabMapTransformer implements Transformer<List<MenuItem>, Map<Class<?>, Tab>> {

    @Override
    public Map<Class<?>, Tab> transform(List<MenuItem> menuItems) {
        final int authOption = VaadinSecurity.check().isAuthenticated() ? 1 : 2;
        return menuItems.stream()
            .filter(menuItem -> Objects.nonNull(menuItem.getTarget()))
            .filter(menuItem -> (authOption & menuItem.getOption()) == 0)
            .map(menuItem -> Tuple.tuple(menuItem.getTarget(), new Tab(createComponent(menuItem))))
            .collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));
    }

    private Component createComponent(MenuItem menuItem) {
        return Optional.ofNullable(menuItem.getIcon())
            .map(icon -> new HorizontalLayout(icon.create(), new RouterLink(menuItem.getLabel(), menuItem.getTarget())))
            .orElseGet(() -> new HorizontalLayout(new RouterLink(menuItem.getLabel(), menuItem.getTarget())));
    }

}
