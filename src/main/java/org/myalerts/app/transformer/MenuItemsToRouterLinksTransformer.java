package org.myalerts.app.transformer;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.RouterLink;
import org.apache.commons.lang3.StringUtils;

import org.myalerts.app.model.MenuItem;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public class MenuItemsToRouterLinksTransformer implements Transformer<List<MenuItem>, List<RouterLink>> {

    @Override
    public List<RouterLink> transform(final List<MenuItem> menuItems) {
        return Optional.ofNullable(menuItems)
            .orElse(Collections.emptyList())
            .stream()
            .filter(menuItem -> Objects.nonNull(menuItem.getTarget()))
            .filter(menuItem -> menuItem.getRole().validate())
            .map(this::createRouterLink)
            .collect(Collectors.toList());
    }

    private RouterLink createRouterLink(final MenuItem menuItem) {
        final var routerLink = new RouterLink();
        routerLink.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
        routerLink.setRoute(menuItem.getTarget());

        final var icon = new Span();
        icon.addClassNames("me-s", "text-l");
        if (StringUtils.isNotEmpty(menuItem.getIcon())) {
            icon.addClassNames(menuItem.getIcon());
        }

        final var text = new Span(routerLink.getTranslation(menuItem.getLabel()));
        text.addClassNames("font-medium", "text-s");

        routerLink.add(icon, text);
        return routerLink;
    }

}
