package com.portkullis.projectdesigner.adapter;

import com.portkullis.projectdesigner.engine.AssignmentEngine;
import com.portkullis.projectdesigner.model.Project;

import java.util.Collection;
import java.util.HashSet;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;

public class ProjectAssignmentDataAdapter<A, R> implements AssignmentEngine.ProjectData<A, R> {

    private final Project<A, R> project;

    public ProjectAssignmentDataAdapter(Project<A, R> project) {
        this.project = project;
    }

    @Override
    public Collection<R> getResources() {
        return unmodifiableSet(project.getResources());
    }

    @Override
    public Collection<A> getActivities() {
        return unmodifiableList(project.getUtilityData());
    }

    @Override
    public void assignActivityToResource(A activity, R resource) {
        if (!project.getActivityAssignments().containsKey(activity)) {
            project.getActivityAssignments().put(activity, new HashSet<>());
        }
        project.getActivityAssignments().get(activity).add(resource);
    }

}
