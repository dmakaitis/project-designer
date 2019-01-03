package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.engine.AssignmentEngine;
import com.portkullis.projectdesigner.exception.ProjectDesignerRuntimeException;
import com.portkullis.projectdesigner.model.Project;

import java.util.Collection;

public class AssignmentEngineImpl<A, R> implements AssignmentEngine<A, R> {

    @Override
    public void assignResources(Project<A, R> project) {
//        List<A> unassignedActivities = getUnassignedActivities(project);
//        while (!unassignedActivities.isEmpty()) {
//            A activity = unassignedActivities.get(0);
//            String resourceType = project.getActivityTypes().get(activity);
//            SortedSet<R> resources = project.getResourceTypes().get(resourceType);
//            assignResourceToActivity(project, resources.first(), activity);
//
//            unassignedActivities = getUnassignedActivities(project);
//        }
    }

//    private List<A> getUnassignedActivities(Project<A, R> project) {
//        return project.getUtilityData()
//                .stream()
//                .filter(a -> isEmpty(project.getActivityAssignments().get(a)))
//                .sorted(activityComparator)
//                .collect(toList());
//    }

    @Override
    public void assignResourceToActivity(ProjectData<A, R> project, R resource, A activity) {
        if (!project.getResources().contains(resource)) {
            throw new ProjectDesignerRuntimeException("Invalid project resource: " + resource);
        }
        if (!project.getActivities().contains(activity)) {
            throw new ProjectDesignerRuntimeException("Invalid project activity: " + activity);
        }

        project.assignActivityToResource(activity, resource);
    }

    private static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

}
