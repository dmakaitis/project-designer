package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.model.EdgeProperties;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Utility class for calculating float for edges on an arrow diagram.
 *
 * @param <A> the activity type.
 */
class FloatCalculator<A> {

    private final Graph<A> graph;
    private final Function<A, EdgeProperties> propertyMapper;

    private final HashMap<Edge<A>, Integer> floatCache = new HashMap<>();
    private final HashMap<Node, Integer> earlyStartCache = new HashMap<>();
    private final HashMap<Node, Integer> lateStartCache = new HashMap<>();

    /**
     * Constructs the calculator.
     *
     * @param graph          the activity graph.
     * @param propertyMapper the edge property mapper.
     */
    FloatCalculator(Graph<A> graph, Function<A, EdgeProperties> propertyMapper) {
        this.graph = graph;
        this.propertyMapper = propertyMapper;
    }

    /**
     * Calculates the total float for the given edge.
     *
     * @param edge the edge for which to calculate the total float.
     * @return the total float for the edge.
     */
    int getTotalFloat(Edge<A> edge) {
        return floatCache.computeIfAbsent(edge, e -> {
            int earlyStart = getEarlyStart(e.getStart());
            int lateFinish = getLateStart(e.getEnd());
            return lateFinish - (earlyStart + (e.getData().isPresent() ? propertyMapper.apply(e.getData().get()).getDuration() : 0));
        });
    }

    private int getEarlyStart(Node node) {
        return earlyStartCache.computeIfAbsent(node, n -> graph.getEdges().stream()
                .filter(e -> e.getEnd().equals(n))
                .map(e -> getEarlyStart(e.getStart()) + (e.getData().isPresent() ? propertyMapper.apply(e.getData().get()).getDuration() : 0))
                .max(Integer::compareTo)
                .orElse(0)
        );
    }

    private int getLateStart(Node node) {
        return lateStartCache.computeIfAbsent(node, n -> graph.getEdges().stream()
                .filter(e -> e.getStart().equals(n))
                .map(e -> getLateStart(e.getEnd()) - (e.getData().isPresent() ? propertyMapper.apply(e.getData().get()).getDuration() : 0))
                .min(Integer::compareTo)
                .orElse(getEarlyStart(node))
        );
    }

}
