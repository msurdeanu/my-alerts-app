package org.myalerts.component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.renderer.TemplateRenderer;

import org.myalerts.model.StatisticsGroup;
import org.myalerts.model.StatisticsItem;
import org.myalerts.provider.StatisticsProvider;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public class StatisticsTreeGrid extends Composite<VerticalLayout> {

    private final Map<StatisticsItem, List<StatisticsItem>> statsMap;

    public StatisticsTreeGrid(final List<StatisticsProvider> statisticsProviders) {
        statsMap = buildAllStatistics(statisticsProviders);
    }

    @Override
    protected VerticalLayout initContent() {
        final var layout = super.initContent();

        layout.setSizeFull();

        final var treegrid = new TreeGrid<StatisticsItem>();
        treegrid.setItems(statsMap.keySet(), parent -> statsMap.getOrDefault(parent, List.of()));
        treegrid.addColumn(TemplateRenderer.<StatisticsItem>of("<vaadin-grid-tree-toggle "
                    + "leaf='[[item.leaf]]' expanded='{{expanded}}' level='[[level]]'>"
                    + "<vaadin-icon icon='[[item.icon]]'></vaadin-icon>&nbsp;&nbsp;"
                    + "[[item.name]]"
                    + "</vaadin-grid-tree-toggle>")
                .withProperty("leaf", item -> !treegrid.getDataCommunicator().hasChildren(item))
                .withProperty("icon", StatisticsItem::getIcon)
                .withProperty("name", prop -> getTranslation(prop.getName())))
            .setHeader(getTranslation("statistics.property.column"));
        treegrid.addColumn(StatisticsItem::getValue).setHeader(getTranslation("statistics.value.column"));
        treegrid.addColumn(prop -> getTranslation(prop.getDescription())).setHeader(getTranslation("statistics.description.column"));
        treegrid.expandRecursively(statsMap.keySet(), 2);

        layout.add(treegrid);
        return layout;
    }

    private Map<StatisticsItem, List<StatisticsItem>> buildAllStatistics(final List<StatisticsProvider> statisticsProviders) {
        return statisticsProviders.stream()
            .map(StatisticsProvider::getStatisticsGroup)
            .collect(Collectors.toMap(StatisticsGroup::getRoot, StatisticsGroup::getLeafs, (it1, it2) -> it1));
    }

}
