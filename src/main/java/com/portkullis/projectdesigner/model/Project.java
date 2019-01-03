package com.portkullis.projectdesigner.model;

import java.util.*;

/**
 * A software development project.
 *
 * @author darius
 */
public class Project<A, R> {

    private List<A> utilityData;

    private Map<A, String> activityTypes;
    private Map<String, SortedSet<R>> resourceTypes;
    private Set<R> resources;

    private Map<A, Set<R>> activityAssignments;
//    private Map<R, SortedSet<A>> resourceAssignments;

    /**
     * Returns the utility data for the project.
     *
     * @return the project utility data.
     */
    public List<A> getUtilityData() {
        if (utilityData == null) {
            utilityData = new ArrayList<>();
        }
        return utilityData;
    }

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
     * Returns the mapping of activities to resource types.
     *
     * @return the activity resource type map.
     */
    public Map<A, String> getActivityTypes() {
        if (activityTypes == null) {
            activityTypes = new HashMap<>();
        }
        return activityTypes;
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

//    /**
//     * Returns a map of resources to the activities to which they have been assigned. Activities are sorted in the order
//     * in which the resource will perform the activities.
//     *
//     * @return a resource assignment map.
//     */
//    public Map<R, SortedSet<A>> getResourceAssignments() {
//        if (resourceAssignments == null) {
//            resourceAssignments = new HashMap<>();
//        }
//        return resourceAssignments;
//    }

}
