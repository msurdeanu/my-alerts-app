package org.myalerts.transformer;

import org.myalerts.component.AppNavItem;
import org.myalerts.domain.MenuItem;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.vaadin.lineawesome.LineAwesomeIcon.valueOf;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public final class MenuItemsToAppNavItemsTransformer implements Transformer<List<MenuItem>, List<AppNavItem>> {

    @Override
    public List<AppNavItem> transform(final List<MenuItem> menuItems) {
        return ofNullable(menuItems)
                .orElse(List.of())
                .stream()
                .filter(menuItem -> Objects.nonNull(menuItem.getTarget()))
                .filter(menuItem -> menuItem.getRole().validate())
                .map(this::createRouterLink)
                .collect(Collectors.toList());
    }

    private AppNavItem createRouterLink(final MenuItem menuItem) {
        return new AppNavItem(menuItem.getLabel(), menuItem.getTarget(),
                valueOf(menuItem.getIcon().toUpperCase()).create());
    }

}
