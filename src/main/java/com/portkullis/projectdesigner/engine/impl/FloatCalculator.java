package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.engine.VisualizationEngine;

import java.util.HashMap;

/**
 * Utility class for calculating float for edges on an arrow diagram.
 */
class FloatCalculator {

    private final Graph<VisualizationEngine.ActivityData> graph;

    private final HashMap<Edge<VisualizationEngine.ActivityData>, Integer> floatCache = new HashMap<>();
    private final HashMap<Node, Integer> earlyStartCache = new HashMap<>();
    private final HashMap<Node, Integer> lateStartCache = new HashMap<>();

    /**
     * Constructs the calculator.
     *
     * @param graph the activity graph.
     */
    FloatCalculator(Graph<VisualizationEngine.ActivityData> graph) {
        this.graph = graph;
    }

    /**
     * Calculates the total float for the given edge.
     *
     * @param edge the edge for which to calculate the total float.
     * @return the total float for the edge.
     */
    int getTotalFloat(Edge<VisualizationEngine.ActivityData> edge) {
        return floatCache.computeIfAbsent(edge, e -> {
            int earlyStart = getEarlyStart(e.getStart());
            int lateFinish = getLateStart(e.getEnd());
            return lateFinish - (earlyStart + (e.getData().isPresent() ? e.getData().get().getEdgeProperties().getDuration() : 0));
        });
    }

    private int getEarlyStart(Node node) {
        return earlyStartCache.computeIfAbsent(node, n -> graph.getEdges().stream()
                .filter(e -> e.getEnd().equals(n))
                .map(e -> getEarlyStart(e.getStart()) + (e.getData().isPresent() ? e.getData().get().getEdgeProperties().getDuration() : 0))
                .max(Integer::compareTo)
                .orElse(0)
        );
    }

    private int getLateStart(Node node) {
        return lateStartCache.computeIfAbsent(node, n -> graph.getEdges().stream()
                .filter(e -> e.getStart().equals(n))
                .map(e -> getLateStart(e.getEnd()) - (e.getData().isPresent() ? e.getData().get().getEdgeProperties().getDuration() : 0))
                .min(Integer::compareTo)
                .orElse(getEarlyStart(node))
        );
    }

}
