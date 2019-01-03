package com.portkullis.projectdesigner.engine;

import com.portkullis.projectdesigner.model.EdgeProperties;

import java.util.Collection;

/**
 * Engine for creating project visualizations.
 */
public interface VisualizationEngine {

    void visualizeProject(ProjectData project);

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
         * Returns the prerequisites for the activity.
         *
         * @return the prerequisites for the activity.
         */
        Collection<ActivityData> getPrerequisites();

        /**
         * Returns the edge properties to use for the activity in the visualization.
         *
         * @return the edge properties for the activity.
         */
        EdgeProperties getEdgeProperties();

    }

}
