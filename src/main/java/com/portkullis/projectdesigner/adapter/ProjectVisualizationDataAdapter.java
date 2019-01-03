package com.portkullis.projectdesigner.adapter;

import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.model.Activity;
import com.portkullis.projectdesigner.model.EdgeProperties;
import com.portkullis.projectdesigner.model.Project;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class ProjectVisualizationDataAdapter implements VisualizationEngine.ProjectData {

    private final Project<Activity, ?> project;

    private final transient Map<Activity, VisualizationEngine.ActivityData> activityDataMap = new HashMap<>();

    public ProjectVisualizationDataAdapter(Project<Activity, ?> project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectVisualizationDataAdapter that = (ProjectVisualizationDataAdapter) o;
        return Objects.equals(project, that.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(project);
    }

    @Override
    public Collection<VisualizationEngine.ActivityData> getActivities() {
        return project.getUtilityData().stream()
                .map(this::wrapActivity)
                .collect(toSet());
    }

    private VisualizationEngine.ActivityData wrapActivity(Activity activity) {
        return activityDataMap.computeIfAbsent(activity, ActivityVisualizationDataAdapter::new);
    }

    private class ActivityVisualizationDataAdapter implements VisualizationEngine.ActivityData {

        private final Activity activity;

        private ActivityVisualizationDataAdapter(Activity activity) {
            this.activity = activity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ActivityVisualizationDataAdapter that = (ActivityVisualizationDataAdapter) o;
            return Objects.equals(activity, that.activity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(activity);
        }

        @Override
        public Collection<VisualizationEngine.ActivityData> getPrerequisites() {
            Set<VisualizationEngine.ActivityData> prerequisites = activity.getPrerequisites().stream()
                    .map(ActivityVisualizationDataAdapter::new)
                    .collect(toSet());

            // TODO: Include resource dependencies
            Set<?> activityResources = project.getActivityAssignments().get(activity);
            if (activityResources != null) {
                activityResources.forEach(resource -> {
                    Set<Activity> possiblePrerequisites = project.getActivityAssignments().entrySet().stream()
                            .filter(e -> e.getValue().contains(resource))
                            .map(Map.Entry::getKey)
                            .collect(toSet());
                    System.out.println("---- " + activity.getId() + " => " + possiblePrerequisites);
                });
            }

            // TODO: Include resource type dependencies

            return prerequisites;
        }

        @Override
        public EdgeProperties getEdgeProperties() {
            return new EdgeProperties(Long.toString(activity.getId()), activity.getDuration());
        }

    }

}
