package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.engine.AssignmentEngine;
import com.portkullis.projectdesigner.exception.ProjectDesignerRuntimeException;
import com.portkullis.projectdesigner.model.Project;

import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeSet;

public class AssignmentEngineImpl<A, R> implements AssignmentEngine<A, R> {

    private final Comparator<A> activityComparator;

    public AssignmentEngineImpl(Comparator<A> activityComparator) {
        this.activityComparator = activityComparator;
    }

    @Override
    public void assignResources(Project<A, R> project) {

    }

    @Override
    public void assignResourceToActivity(Project<A, R> project, R resource, A activity) {
        if (!project.getResources().contains(resource)) {
            throw new ProjectDesignerRuntimeException("Invalid project resource: " + resource);
        }
        if (!project.getUtilityData().contains(activity)) {
            throw new ProjectDesignerRuntimeException("Invalid project activity: " + activity);
        }

        if (!project.getActivityAssignments().containsKey(activity)) {
            project.getActivityAssignments().put(activity, new HashSet<>());
        }
        project.getActivityAssignments().get(activity).add(resource);

        if (!project.getResourceAssignments().containsKey(resource)) {
            project.getResourceAssignments().put(resource, new TreeSet<>(activityComparator));
        }
        project.getResourceAssignments().get(resource).add(activity);
    }

}
