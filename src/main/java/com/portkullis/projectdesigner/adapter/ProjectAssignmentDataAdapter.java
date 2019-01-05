package com.portkullis.projectdesigner.adapter;

import com.portkullis.projectdesigner.engine.AssignmentEngine;
import com.portkullis.projectdesigner.model.Activity;
import com.portkullis.projectdesigner.model.Project;
import com.portkullis.projectdesigner.model.Span;
import com.portkullis.projectdesigner.model.SpanSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedSet;

import static com.portkullis.projectdesigner.model.SpanSet.asUnion;
import static java.util.Collections.*;
import static java.util.stream.Collectors.toList;

/**
 * Project data adapter for the assignment engine.
 */
public class ProjectAssignmentDataAdapter extends AbstractProjectDataAdapter implements AssignmentEngine.ProjectData<Activity, String> {

    /**
     * Constructs the adapter.
     *
     * @param project the project to adapt.
     */
    public ProjectAssignmentDataAdapter(Project<Activity, String> project) {
        super(project);
    }

    @Override
    public Collection<String> getResources() {
        return unmodifiableSet(project.getResources());
    }

    @Override
    public Collection<Activity> getActivities() {
        return unmodifiableList(project.getUtilityData());
    }

    @Override
    public Collection<Activity> getUnassignedActivities() {
        return unmodifiableList(project.getUtilityData().stream()
                .filter(a -> project.getActivityAssignments().getOrDefault(a, emptySet()).isEmpty())
                .collect(toList())
        );
    }

    @Override
    public int getEarliestStart(Activity activity) {
        return getEarlyStartFromGraph(activity);
    }

    @Override
    public int getEarliestFinish(Activity activity) {
        return getEarlyStartFromGraph(activity) + activity.getDuration();
    }

    @Override
    public int getTotalFloat(Activity activity) {
        return getLateStartFromGraph(activity) - getEarlyStartFromGraph(activity);
    }

    @Override
    public void assignActivityToResource(Activity activity, String resource) {
        if (!project.getActivityAssignments().containsKey(activity)) {
            project.getActivityAssignments().put(activity, new HashSet<>());
        }
        project.getActivityAssignments().get(activity).add(resource);
    }

    @Override
    public String getResourceType(Activity activity) {
        return project.getActivityTypes().get(activity);
    }

    @Override
    public SortedSet<String> getResourcesOfType(String resourceType) {
        return project.getResourceTypes().get(resourceType);
    }

    @Override
    public SpanSet<Activity> getResourceOccupiedSpans(String resource) {
        return project.getActivityAssignments().entrySet().stream()
                .filter(e -> e.getValue().contains(resource))
                .map(Map.Entry::getKey)
                .map(a -> new Span<>(getEarliestStart(a), getEarliestFinish(a), a))
                .collect(asUnion());
    }

}
