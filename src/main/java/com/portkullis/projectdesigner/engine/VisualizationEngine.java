package com.portkullis.projectdesigner.engine;

import com.portkullis.projectdesigner.engine.impl.Graph;
import com.portkullis.projectdesigner.model.Activity;
import com.portkullis.projectdesigner.model.EdgeProperties;

import java.util.Collection;

/**
 * Engine for creating project visualizations.
 */
public interface VisualizationEngine {

    /**
     * Visualizes a project as an arrow diagram.
     *
     * @param project the project data.
     */
    void visualizeProject(ProjectData project);

    /**
     * Visualizes a graph.
     *
     * @param graph the graph to visualize.
     */
    void visualizeGraph(Graph<ActivityData> graph);

    /**
     * Interface to project data required by the visualization engine.
     */
    interface ProjectData {

        /**
         * Returns all the activities in the project.
         *
         * @return all the activities in the project.
         */
        Collection<ActivityData> getActivities();

    }

    /**
     * Interface to activity data required by the visualization engine.
     */
    interface ActivityData {

        /**
         * Returns the underlying activity.
         *
         * @return the underlying activity.
         */
        Activity getActivity();

        /**
         * Returns the duration of the activity.
         *
         * @return the duration of the activity.
         */
        int getDuration();

        /**
         * Returns the direct prerequisites for the activity.
         *
         * @return the direct prerequisites for the activity.
         */
        Collection<ActivityData> getPrerequisites();

        /**
         * Returns the earliest start date for the activity.
         *
         * @return the earliest start date for the activity.
         */
        int getEarlyStart();

        /**
         * Returns the latest start date for the activity without affecting the total duration of the project.
         *
         * @return the latest start date for the activity.
         */
        int getLateStart();

        /**
         * Returns the edge properties to use for the activity in the visualization.
         *
         * @return the edge properties for the activity.
         */
        EdgeProperties getEdgeProperties();

    }

}
