package com.portkullis.projectdesigner.adapter;

import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.model.Activity;
import com.portkullis.projectdesigner.model.EdgeProperties;
import com.portkullis.projectdesigner.model.Project;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Data adapter to allow the visualization engine to deal with projects without having to know the project data
 * structures.
 *
 * @author darius
 */
public class ProjectVisualizationDataAdapter implements VisualizationEngine.ProjectData {

    private final Project<Activity, ?> project;

    private final transient Map<Activity, VisualizationEngine.ActivityData> activityDataMap = new HashMap<>();

    /**
     * Constructs the adapter.
     *
     * @param project the project for which to provide data.
     */
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
        public int getDuration() {
            return activity.getDuration();
        }

        @Override
        public Collection<VisualizationEngine.ActivityData> getPrerequisites() {
            Set<VisualizationEngine.ActivityData> prerequisites = activity.getPrerequisites().stream()
                    .map(ActivityVisualizationDataAdapter::new)
                    .collect(toSet());

            Set<?> activityResources = project.getActivityAssignments().get(activity);
            if (activityResources != null) {
                activityResources.forEach(resource -> {
                    List<VisualizationEngine.ActivityData> possiblePrerequisites = project.getActivityAssignments().entrySet().stream()
                            .filter(e -> !e.getKey().equals(this))
                            .filter(e -> e.getValue().contains(resource))
                            .map(Map.Entry::getKey)
                            .map(ProjectVisualizationDataAdapter.this::wrapActivity)
                            .sorted(comparing(VisualizationEngine.ActivityData::getEarlyStart).thenComparing(VisualizationEngine.ActivityData::getLateStart))
                            .collect(toList());

                    possiblePrerequisites = possiblePrerequisites.subList(0, possiblePrerequisites.indexOf(this));

                    prerequisites.addAll(possiblePrerequisites);
                });
            }

            // TODO: Include resource type dependencies

            return prerequisites;
        }

        @Override
        public int getLateStart() {
//            System.out.println("Getting late start for activity " + activity.getId());
            return getSuccessors().stream()
                    .mapToInt(s -> s.getLateStart() - getDuration())
                    .min()
                    .orElse(getEarlyStart());
        }

        @Override
        public int getEarlyStart() {
            return activity.getPrerequisites().stream()
                    .map(ProjectVisualizationDataAdapter.this::wrapActivity)
                    .mapToInt(p -> p.getEarlyStart() + p.getDuration())
                    .max()
                    .orElse(0);
        }

        @Override
        public EdgeProperties getEdgeProperties() {
            return new EdgeProperties(Long.toString(activity.getId()), activity.getDuration());
        }

        private Collection<VisualizationEngine.ActivityData> getSuccessors() {
            return project.getUtilityData().stream()
                    .filter(a -> a.getPrerequisites().stream().anyMatch(p -> wrapActivity(p).equals(this)))
                    .map(ProjectVisualizationDataAdapter.this::wrapActivity)
                    .collect(toSet());
        }

    }

}
