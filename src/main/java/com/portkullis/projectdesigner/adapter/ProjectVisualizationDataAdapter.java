package com.portkullis.projectdesigner.adapter;

import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.engine.impl.Edge;
import com.portkullis.projectdesigner.engine.impl.Graph;
import com.portkullis.projectdesigner.engine.impl.Node;
import com.portkullis.projectdesigner.model.Activity;
import com.portkullis.projectdesigner.model.EdgeProperties;
import com.portkullis.projectdesigner.model.Project;
import com.portkullis.projectdesigner.model.Span;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

/**
 * Data adapter to allow the visualization engine to deal with projects without having to know the project data
 * structures.
 *
 * @author darius
 */
public class ProjectVisualizationDataAdapter<R> implements VisualizationEngine.ProjectData {

    private final Project<Activity, R> project;

    private final transient Map<Activity, VisualizationEngine.ActivityData> activityDataMap = new HashMap<>();

    private final Map<Activity, Node> activityNodes = new HashMap<>();
    private final Map<Node, Activity> nodeActivities = new HashMap<>();
    private Graph<Object> activityGraph = null;

    /**
     * Constructs the adapter.
     *
     * @param project the project for which to provide data.
     */
    public ProjectVisualizationDataAdapter(Project<Activity, R> project) {
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

    @Override
    public List<Span<VisualizationEngine.ActivityData>> getResourceTypeOccupiedSpans(String resourceType) {
        SortedSet<R> resources = project.getResourceTypes().get(resourceType);
        Map<R, List<Span<VisualizationEngine.ActivityData>>> resourceSpans = resources.stream().collect(toMap(identity(), r -> project.getActivityAssignments().entrySet().stream()
                .filter(e -> e.getValue().contains(r))
                .map(Map.Entry::getKey)
                .map(this::wrapActivity)
                .sorted(comparing(VisualizationEngine.ActivityData::getEarlyStart).thenComparing(VisualizationEngine.ActivityData::getLateStart))
                .map(a -> new Span<>(a.getEarlyStart(), a.getEarlyStart() + a.getDuration(), a))
                .collect(toList())
        ));

        System.out.println(resourceSpans);

        return resourceSpans.values().stream().findFirst().orElse(new ArrayList<>());
    }

    private VisualizationEngine.ActivityData wrapActivity(Activity activity) {
        return activityDataMap.computeIfAbsent(activity, ActivityVisualizationDataAdapter::new);
    }

    public Graph<Object> getActivityGraph() {
        if (activityGraph == null) {
            activityNodes.clear();
            nodeActivities.clear();

            activityGraph = new Graph<>();

            // Create nodes for each activity
            project.getUtilityData().forEach(a -> {
                Node node = new Node(a.getId(), Long.toString(a.getId()));
                activityNodes.put(a, node);
                nodeActivities.put(node, a);
                activityGraph.getNodes().add(node);
            });

            // Now add edges for the utility data prerequisites
            project.getUtilityData().forEach(a -> {
                Node activityNode = activityNodes.get(a);
                a.getPrerequisites().forEach(p -> {
                    Node prerequisiteNode = activityNodes.get(p);
                    activityGraph.getEdges().add(new Edge<>(prerequisiteNode, activityNode));
                });
            });

            // TODO: Add edges for resource dependencies
            project.getResources().forEach(r -> {
                project.getActivityAssignments().entrySet().stream()
                        .filter(e -> e.getValue().contains(r))
                        .map(Map.Entry::getKey)
                        .sorted(comparing(a -> getEarlyStartFromGraph(activityGraph, a)))
                        .reduce(null, (p, a) -> {
                            if (p != null) {
                                Node previous = activityNodes.get(p);
                                Node next = activityNodes.get(a);
                                activityGraph.getEdges().add(new Edge<>(previous, next));
                            }
                            return a;
                        });
            });
        }

        return activityGraph;
    }

    private int getEarlyStartFromGraph(Graph<Object> activityGraph, Activity activity) {
        Node node = activityNodes.get(activity);
        return activityGraph.getEdges().stream()
                .filter(e -> e.getEnd().equals(node))
                .mapToInt(e -> {
                    Activity prereq = nodeActivities.get(e.getStart());
                    return getEarlyStartFromGraph(activityGraph, prereq) + prereq.getDuration();
                })
                .min()
                .orElse(0);
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
            // Initialize the activity graph...
            getActivityGraph();

            Node activityNode = activityNodes.get(activity);

            return getActivityGraph().getEdges().stream()
                    .filter(e -> e.getEnd().equals(activityNode))
                    .map(Edge::getStart)
                    .map(nodeActivities::get)
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
            return getEarlyStartFromGraph(getActivityGraph(), activity);
        }

        @Override
        public EdgeProperties getEdgeProperties() {
            return new EdgeProperties(Long.toString(activity.getId()), activity.getDuration());
        }

        private Collection<VisualizationEngine.ActivityData> getSuccessors() {
            // Initialize the activity graph...
            getActivityGraph();

            Node activityNode = activityNodes.get(activity);

            return getActivityGraph().getEdges().stream()
                    .filter(e -> e.getStart().equals(activityNode))
                    .map(Edge::getEnd)
                    .map(nodeActivities::get)
                    .map(ProjectVisualizationDataAdapter.this::wrapActivity)
                    .collect(toSet());
        }

//        @Override
//        public String getActivityResourceType() {
//            return project.getActivityTypes().get(activity);
//        }

    }

}
