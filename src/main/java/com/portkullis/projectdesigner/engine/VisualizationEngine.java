package com.portkullis.projectdesigner.engine;

import com.portkullis.projectdesigner.model.Graph;

public interface VisualizationEngine<T> {

    void visualizeGraph(Graph<T> graph);

}
