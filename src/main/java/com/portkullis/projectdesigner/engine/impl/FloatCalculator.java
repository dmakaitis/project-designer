package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.model.EdgeProperties;

import java.util.HashMap;
import java.util.function.Function;

public class FloatCalculator<A> {

    private final Graph<A> graph;
    private final Function<A, EdgeProperties> propertyMapper;

    private final HashMap<Edge<A>, Integer> floatCache = new HashMap<>();
    private final HashMap<Node, Integer> earlyStartCache = new HashMap<>();
    private final HashMap<Node, Integer> lateStartCache = new HashMap<>();

    public FloatCalculator(Graph<A> graph, Function<A, EdgeProperties> propertyMapper) {
        this.graph = graph;
        this.propertyMapper = propertyMapper;
    }

    public int getTotalFloat(Edge<A> edge) {
        return floatCache.computeIfAbsent(edge, e -> {
            int earlyStart = getEarlyStart(e.getStart());
            int lateFinish = getLateStart(e.getEnd());
            return lateFinish - (earlyStart + (e.getData() == null ? 0 : propertyMapper.apply(e.getData()).getDuration()));
        });
    }

    public int getEarlyStart(Node node) {
        return earlyStartCache.computeIfAbsent(node, n -> graph.getEdges().stream()
                .filter(e -> e.getEnd().equals(n))
                .map(e -> getEarlyStart(e.getStart()) + (e.getData() == null ? 0 : propertyMapper.apply(e.getData()).getDuration()))
                .max(Integer::compareTo)
                .orElse(0)
        );
    }

    public int getLateStart(Node node) {
        return lateStartCache.computeIfAbsent(node, n -> graph.getEdges().stream()
                .filter(e -> e.getStart().equals(n))
                .map(e -> getLateStart(e.getEnd()) - (e.getData() == null ? 0 : propertyMapper.apply(e.getData()).getDuration()))
                .min(Integer::compareTo)
                .orElse(getEarlyStart(node))
        );
    }

}
