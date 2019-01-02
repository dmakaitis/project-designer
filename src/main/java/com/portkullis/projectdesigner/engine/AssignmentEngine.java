package com.portkullis.projectdesigner.engine;

import com.portkullis.projectdesigner.model.Project;

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
    void assignResources(Project<A, R> project);

    /**
     * Assigns a single resource to a single activity. Multiple calls to this method may be used to assign multiple
     * resources to an activity, or to assign multiple activities to a resource.
     *
     * @param project  the project for which to assign resources.
     * @param resource the resource to assign.
     * @param activity the activity to which to assign the resource.
     */
    void assignResourceToActivity(Project<A, R> project, R resource, A activity);

}
