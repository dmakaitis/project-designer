package com.portkullis.projectdesigner.engine;

import com.portkullis.projectdesigner.engine.impl.Graph;
import com.portkullis.projectdesigner.model.Activity;
import com.portkullis.projectdesigner.model.EdgeProperties;
import com.portkullis.projectdesigner.model.SpanSet;

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

        /**
         * Returns the spans of time during which all the resources of the given resource type are fully occupied during
         * the project.
         *
         * @param resourceType the resource type.
         * @return the spans of time during which all resources of the given type are fully occupied.
         */
        SpanSet<ActivityData> getResourceTypeOccupiedSpans(String resourceType);

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

//        /**
//         * Returns the type of resource that must perform this activity.
//         *
//         * @return the type of resource that must perform this activity, or {@code null} if any resource can perform this activity.
//         */
//        String getActivityResourceType();

    }

}
