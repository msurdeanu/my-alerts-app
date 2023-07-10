package org.myalerts.transformer;

import com.vaadin.flow.component.sidenav.SideNavItem;
import org.myalerts.domain.MenuItem;

import java.util.List;
import java.util.stream.Collectors;

import static com.vaadin.flow.component.icon.VaadinIcon.valueOf;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public final class MenuItemsToSideNavItemsTransformer implements Transformer<List<MenuItem>, List<SideNavItem>> {

    @Override
    public List<SideNavItem> transform(final List<MenuItem> menuItems) {
        return ofNullable(menuItems)
                .orElse(List.of())
                .stream()
                .filter(menuItem -> nonNull(menuItem.getTarget()))
                .filter(menuItem -> menuItem.getRole().validate())
                .map(this::createRouterLink)
                .collect(Collectors.toList());
    }

    private SideNavItem createRouterLink(final MenuItem menuItem) {
        final var sideNavItem = new SideNavItem(menuItem.getLabel(), menuItem.getTarget(),
                valueOf(menuItem.getIcon().toUpperCase()).create());
        sideNavItem.setLabel(sideNavItem.getTranslation(menuItem.getLabel()));
        return sideNavItem;
    }

}
