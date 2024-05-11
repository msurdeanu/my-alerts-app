package org.myalerts.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.RequiredArgsConstructor;
import org.pf4j.PluginWrapper;
import org.vaadin.klaudeta.PaginatedGrid;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RequiredArgsConstructor
public final class PluginGrid extends Composite<VerticalLayout> {

    private final PaginatedGrid<PluginWrapper, ?> paginatedGrid = new PaginatedGrid<>();

    public void setDataProvider(final DataProvider<PluginWrapper, ?> dataProvider) {
        paginatedGrid.setDataProvider(dataProvider);
    }

    @Override
    protected VerticalLayout initContent() {
        final var layout = super.initContent();

        layout.setSizeFull();
        paginatedGrid.setAllRowsVisible(true);
        paginatedGrid.addColumn(new ComponentRenderer<>(this::renderId))
            .setHeader(getTranslation("plugin.main-grid.id.column"))
            .setAutoWidth(true);
        paginatedGrid.addColumn(new ComponentRenderer<>(this::renderDescription))
            .setHeader(getTranslation("plugin.main-grid.description.column"))
            .setAutoWidth(true);
        paginatedGrid.addColumn(new ComponentRenderer<>(this::renderVersion))
            .setHeader(getTranslation("plugin.main-grid.version.column"))
            .setAutoWidth(true);
        paginatedGrid.addColumn(new ComponentRenderer<>(this::renderStatus))
            .setHeader(getTranslation("plugin.main-grid.status.column"))
            .setAutoWidth(true);
        paginatedGrid.setPageSize(10);
        paginatedGrid.setPaginatorSize(5);
        paginatedGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        layout.add(paginatedGrid);

        return layout;
    }

    private Component renderId(final PluginWrapper plugin) {
        final var pluginDescriptor = plugin.getDescriptor();
        return new Anchor(pluginDescriptor.getProvider(), pluginDescriptor.getPluginId(), AnchorTarget.BLANK);
    }

    private Component renderDescription(final PluginWrapper plugin) {
        return new NativeLabel(plugin.getDescriptor().getPluginDescription());
    }

    private Component renderVersion(final PluginWrapper plugin) {
        final var pluginDescriptor = plugin.getDescriptor();
        return new Html(getTranslation("plugin.main-grid.version",
            pluginDescriptor.getVersion(), pluginDescriptor.getRequires()));
    }

    private Component renderStatus(final PluginWrapper plugin) {
        final var status = getPluginStatus(plugin);
        final var span = new Span(getTranslation("plugin.main-grid.status." + status));
        span.getElement().getThemeList().add("badge " + status);
        return span;
    }

    private String getPluginStatus(final PluginWrapper plugin) {
        return switch (plugin.getPluginState()) {
            case DISABLED -> "contrast";
            case STOPPED -> "primary";
            case FAILED -> "error";
            default -> "success";
        };
    }

}
