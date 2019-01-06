package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.engine.AssignmentEngine;
import com.portkullis.projectdesigner.exception.ProjectDesignerRuntimeException;
import com.portkullis.projectdesigner.model.Span;

import java.util.Collection;
import java.util.Optional;
import java.util.SortedSet;

import static java.util.Comparator.comparingInt;

public class AssignmentEngineImpl<A, R> implements AssignmentEngine<A, R> {

    @Override
    public void assignResources(ProjectData<A, R> project) {
        Collection<A> unassignedActivities = project.getUnassignedActivities();
        int lastUnassignedActivityCount = 0;

        while (!unassignedActivities.isEmpty() && unassignedActivities.size() != lastUnassignedActivityCount) {
            lastUnassignedActivityCount = unassignedActivities.size();

            unassignedActivities.stream()
                    .filter(a -> project.getResourceType(a) != null)
                    .sorted(comparingInt(project::getEarliestStart).thenComparingInt(project::getTotalFloat))
                    .findFirst()
                    .ifPresent(activity -> {
                        SortedSet<R> candidateResources = project.getResourcesOfType(project.getResourceType(activity));
                        Span<A> activitySpan = new Span<>(project.getEarliestStart(activity), project.getEarliestFinish(activity), activity);
                        Optional<R> firstAvailableResource = candidateResources.stream()
                                .filter(r -> project.getResourceOccupiedSpans(r).intersect(activitySpan).isEmpty())
                                .findFirst();

                        firstAvailableResource.ifPresent(r -> {
                            System.out.println("Assigning " + r + " to " + activity);
                            project.assignActivityToResource(activity, r);
                        });
                    });

            unassignedActivities = project.getUnassignedActivities();
        }
    }

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

}
