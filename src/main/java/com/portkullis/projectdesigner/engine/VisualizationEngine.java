package com.portkullis.projectdesigner.engine;

import com.portkullis.projectdesigner.engine.impl.Graph;

import java.util.Collection;

public interface VisualizationEngine<T> {

    void visualizeUtilityData(Collection<T> utilityData);

    void visualizeGraph(Graph<T> graph);

}
