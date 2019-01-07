package com.portkullis.projectdesigner.model;

import java.util.*;

public class Plan<A, R> {

    private Map<String, SortedSet<R>> resourceTypes;
    private Set<R> resources;

    private Map<A, Set<R>> activityAssignments;

    /**
     * Returns the collection of resources that may be used on the project.
     *
     * @return the collection of resources.
     */
    public Set<R> getResources() {
        if (resources == null) {
            resources = new HashSet<>();
        }
        return resources;
    }

    /**
     * Returns the mapping of resource types to resources. Resources may belong to more than one type.
     *
     * @return the resource type map.
     */
    public Map<String, SortedSet<R>> getResourceTypes() {
        if (resourceTypes == null) {
            resourceTypes = new HashMap<>();
        }
        return resourceTypes;
    }

    /**
     * Returns a map of activities to resources assigned to each activity.
     *
     * @return an activity assignment map.
     */
    public Map<A, Set<R>> getActivityAssignments() {
        if (activityAssignments == null) {
            activityAssignments = new HashMap<>();
        }
        return activityAssignments;
    }

}
