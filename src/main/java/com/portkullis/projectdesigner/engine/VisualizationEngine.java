package com.portkullis.projectdesigner.engine;

import com.portkullis.projectdesigner.engine.impl.Graph;
import com.portkullis.projectdesigner.model.Project;

/**
 * Engine for creating project visualizations.
 *
 * @param <A> the type of activities in the project.
 */
public interface VisualizationEngine<A> {

    void visualizeProject(Project<A> project);

    void visualizeGraph(Graph<A> graph);

}
