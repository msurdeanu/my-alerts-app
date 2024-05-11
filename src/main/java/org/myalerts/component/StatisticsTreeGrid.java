package org.myalerts.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import org.myalerts.domain.StatisticsGroup;
import org.myalerts.domain.StatisticsItem;
import org.myalerts.provider.StatisticsProvider;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public final class StatisticsTreeGrid extends Composite<VerticalLayout> {

    private final Map<StatisticsItem, List<StatisticsItem>> statsMap;

    public StatisticsTreeGrid(final List<StatisticsProvider> statisticsProviders) {
        statsMap = buildAllStatistics(statisticsProviders);
    }

    @Override
    protected VerticalLayout initContent() {
        final var layout = super.initContent();
        layout.setSizeFull();

        final var treeGrid = new TreeGrid<StatisticsItem>();
        treeGrid.setItems(statsMap.keySet(), parent -> statsMap.getOrDefault(parent, List.of()));
        treeGrid.addColumn(LitRenderer.<StatisticsItem>of("<vaadin-grid-tree-toggle "
                    + "leaf=${item.leaf} .expanded=${model.expanded} .level=${model.level}>"
                    + "<vaadin-icon icon='${item.icon}'></vaadin-icon>&nbsp;&nbsp;"
                    + "${item.name}"
                    + "</vaadin-grid-tree-toggle>")
                .withProperty("leaf", item -> !treeGrid.getDataCommunicator().hasChildren(item))
                .withProperty("icon", StatisticsItem::getIcon)
                .withProperty("name", prop -> getTranslation(prop.getName()))
                .withFunction("onClick", item -> {
                    if (treeGrid.getDataCommunicator().hasChildren(item)) {
                        if (treeGrid.isExpanded(item)) {
                            treeGrid.collapse(List.of(item));
                        } else {
                            treeGrid.expand(List.of(item));
                        }
                    }
                }))
            .setHeader(getTranslation("statistics.property.column"));
        treeGrid.addColumn(new ComponentRenderer<>(this::getComputedValue))
            .setHeader(getTranslation("statistics.value.column"));
        treeGrid.expandRecursively(statsMap.keySet(), 2);
        treeGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);

        layout.add(treeGrid);
        return layout;
    }

    private Map<StatisticsItem, List<StatisticsItem>> buildAllStatistics(final List<StatisticsProvider> statisticsProviders) {
        return statisticsProviders.stream()
            .map(StatisticsProvider::getStatisticsGroup)
            .collect(Collectors.toMap(StatisticsGroup::getRoot, StatisticsGroup::getLeafs, (it1, it2) -> it1, LinkedHashMap::new));
    }

    private Component getComputedValue(final StatisticsItem statisticsItem) {
        final var span = new Span(statisticsItem.getValueAsString());
        ofNullable(statisticsItem.getDescription())
            .ifPresent(desc -> Tooltip.forComponent(span)
                .withText(getTranslation(desc))
                .withPosition(Tooltip.TooltipPosition.TOP));
        return span;
    }

}
