package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.model.EdgeProperties;
import com.portkullis.projectdesigner.model.Graph;

import java.util.function.Function;

public class VisualizationEngineImpl<T> implements VisualizationEngine<T> {

    private final Function<T, EdgeProperties> edgePropertyMapper;

    public VisualizationEngineImpl(Function<T, EdgeProperties> edgePropertyMapper) {
        this.edgePropertyMapper = edgePropertyMapper;
    }

    @Override
    public void visualizeGraph(Graph<T> graph) {
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("digraph {\n");
            buffer.append("    rankdir = LR\n");
            graph.getEdges().forEach(e -> {
                buffer.append("    ").append(e.getStart().getLabel()).append(" -> ").append(e.getEnd().getLabel());
                if (e.getData() != null) {
                    EdgeProperties ep = edgePropertyMapper.apply(e.getData());
                    buffer.append(" [ label = \"").append(ep.getLabel()).append("\" ]");
                } else {
                    buffer.append(" [ style = dotted ]");
                }
                buffer.append(";\n");
            });
            buffer.append("}");

            Process dot = Runtime.getRuntime().exec("dot -Tpng -o test.png");
            dot.getOutputStream().write(buffer.toString().getBytes());
            dot.getOutputStream().close();
            dot.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
