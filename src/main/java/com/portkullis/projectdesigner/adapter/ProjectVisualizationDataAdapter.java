package com.portkullis.projectdesigner.adapter;

import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.model.Activity;
import com.portkullis.projectdesigner.model.EdgeProperties;
import com.portkullis.projectdesigner.model.Project;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toSet;

/**
 * Data adapter to allow the visualization engine to deal with projects without having to know the project data
 * structures.
 *
 * @author darius
 */
public class ProjectVisualizationDataAdapter extends AbstractProjectDataAdapter implements VisualizationEngine.ProjectData {

    private final Map<Activity, VisualizationEngine.ActivityData> activityDataMap = new HashMap<>();

    /**
     * Constructs the adapter.
     *
     * @param project the project for which to provide data.
     */
    public ProjectVisualizationDataAdapter(Project<Activity, String> project) {
        super(project);
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

        ActivityVisualizationDataAdapter(Activity activity) {
            this.activity = activity;
        }

        @Override
        public String toString() {
            return "ActivityVisualizationDataAdapter{" +
                    "activity=" + activity +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ActivityVisualizationDataAdapter that = (ActivityVisualizationDataAdapter) o;
            return activity.equals(that.activity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(activity);
        }

        @Override
        public Activity getActivity() {
            return activity;
        }

        @Override
        public int getDuration() {
            return activity.getDuration();
        }

        @Override
        public Collection<VisualizationEngine.ActivityData> getPrerequisites() {
            return ProjectVisualizationDataAdapter.this.getPrerequisites(activity).stream()
                    .map(ProjectVisualizationDataAdapter.this::wrapActivity)
                    .collect(toSet());
        }

        @Override
        public int getLateStart() {
            return getSuccessors().stream()
                    .mapToInt(s -> s.getLateStart() - getDuration())
                    .min()
                    .orElse(getEarlyStart());
        }

        @Override
        public int getEarlyStart() {
            return getEarlyStartFromGraph(activity);
        }

        @Override
        public EdgeProperties getEdgeProperties() {
            return new EdgeProperties(Long.toString(activity.getId()), activity.getDuration());
        }

        private Collection<VisualizationEngine.ActivityData> getSuccessors() {
            return ProjectVisualizationDataAdapter.this.getSuccessors(activity).stream()
                    .map(ProjectVisualizationDataAdapter.this::wrapActivity)
                    .collect(toSet());
        }

    }

}
