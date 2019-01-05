package com.portkullis.projectdesigner.engine;

import com.portkullis.projectdesigner.model.SpanSet;

import java.util.Collection;
import java.util.SortedSet;

/**
 * Methods for assigning resources to activities in a project.
 *
 * @param <A> the activity type.
 * @param <R> the resource type.
 */
public interface AssignmentEngine<A, R> {

    /**
     * Automatically assigns a resources to all activities that do not already have a resource based on each activity's
     * resource type and total float. For each resource type, the first resources in the resource list for the resource
     * type will be used, if available, before later resources.
     *
     * @param project the project for which to assign resources.
     */
    void assignResources(ProjectData<A, R> project);

    /**
     * Assigns a single resource to a single activity. Multiple calls to this method may be used to assign multiple
     * resources to an activity, or to assign multiple activities to a resource.
     *
     * @param project  the project for which to assign resources.
     * @param resource the resource to assign.
     * @param activity the activity to which to assign the resource.
     */
    void assignResourceToActivity(ProjectData<A, R> project, R resource, A activity);

    /**
     * Expected project interface.
     *
     * @param <A> the project activity type.
     * @param <R> the project resource type.
     */
    interface ProjectData<A, R> {

        /**
         * Returns the resources that are defined for the project.
         *
         * @return the resources that are defined for the project.
         */
        Collection<R> getResources();

        /**
         * Returns the activities that are defined for the project.
         *
         * @return the activities that are defined for the project.
         */
        Collection<A> getActivities();

        /**
         * Returns the unassigned activities in the project.
         *
         * @return the unassigned activities in the project.
         */
        Collection<A> getUnassignedActivities();

        /**
         * Returns the earliest start of the activity.
         *
         * @param activity the activity for which to get the earliest start.
         * @return the earliest start of the activity.
         */
        int getEarliestStart(A activity);

        /**
         * Returns the earliest finish of the activity.
         *
         * @param activity the activity for which to get the earliest finish.
         * @return the earliest finish of the activity.
         */
        int getEarliestFinish(A activity);

        /**
         * Returns the total float of the activity.
         *
         * @param activity the activity for which to get the total float.
         * @return the total float of the activity.
         */
        int getTotalFloat(A activity);

        /**
         * Assigns an activity to a resource. An activity may be assigned to more than one resource.
         *
         * @param activity the activity to assign.
         * @param resource the resource to which to assign the activity.
         */
        void assignActivityToResource(A activity, R resource);

        /**
         * Returns the resource type that must perform the activity.
         *
         * @param activity the activity.
         * @return the resource type.
         */
        String getResourceType(A activity);

        /**
         * Returns the list of resources of the given type. The list should be sorted in order of preference with the
         * most preferred resource being at the beginning of the list.
         *
         * @param resourceType the resource type.
         * @return the list of resources in order of preference.
         */
        SortedSet<R> getResourcesOfType(String resourceType);

        /**
         * Returns the spans during which the resource has been assigned to activities.
         *
         * @param resource the resource for which to calculate occupied spans.
         * @return the spans during which the resource is assigned to activities.
         */
        SpanSet<A> getResourceOccupiedSpans(R resource);

    }

}
