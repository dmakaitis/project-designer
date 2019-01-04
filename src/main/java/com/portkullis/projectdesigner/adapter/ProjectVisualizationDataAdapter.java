package com.portkullis.projectdesigner.adapter;

import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.engine.impl.Edge;
import com.portkullis.projectdesigner.engine.impl.Graph;
import com.portkullis.projectdesigner.engine.impl.Node;
import com.portkullis.projectdesigner.model.*;

import java.util.*;

import static com.portkullis.projectdesigner.model.SpanSet.asIntersection;
import static com.portkullis.projectdesigner.model.SpanSet.asUnion;
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

    private final Map<Activity, VisualizationEngine.ActivityData> activityDataMap = new HashMap<>();

    private final Map<Activity, Node> activityNodes = new HashMap<>();
    private final Map<Node, Activity> nodeActivities = new HashMap<>();
    private Graph<VisualizationEngine.ActivityData> activityGraph = null;

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
    public SpanSet<VisualizationEngine.ActivityData> getResourceTypeOccupiedSpans(String resourceType) {
        SortedSet<R> resources = project.getResourceTypes().get(resourceType);
        Map<R, SpanSet<VisualizationEngine.ActivityData>> resourceSpans = resources.stream().collect(toMap(identity(), r -> project.getActivityAssignments().entrySet().stream()
                .filter(e -> e.getValue().contains(r))
                .map(Map.Entry::getKey)
                .map(this::wrapActivity)
                .sorted(comparing(VisualizationEngine.ActivityData::getEarlyStart).thenComparing(VisualizationEngine.ActivityData::getLateStart))
                .map(a -> new Span<>(a.getEarlyStart(), a.getEarlyStart() + a.getDuration(), a))
                .collect(asUnion())
        ));

        return resourceSpans.values().stream()
                .collect(asIntersection());
    }

    private VisualizationEngine.ActivityData wrapActivity(Activity activity) {
        return activityDataMap.computeIfAbsent(activity, ActivityVisualizationDataAdapter::new);
    }

    private void addEdge(Activity a, Activity b) {
        Node nodeA = activityNodes.get(a);
        Node nodeB = activityNodes.get(b);

        if (!getAllPredecessors(nodeA).contains(nodeB)) {
            activityGraph.getEdges().add(new Edge<>(nodeA, nodeB));
        }
    }

    private Set<Node> getAllPredecessors(Node node) {
        Set<Node> directPredecessors = activityGraph.getEdges().stream()
                .filter(e -> e.getEnd().equals(node))
                .map(Edge::getStart)
                .collect(toSet());

        Set<Node> predecessors = new HashSet<>();
        for (Node directPredecessor : directPredecessors) {
            predecessors.add(directPredecessor);
            predecessors.addAll(getAllPredecessors(directPredecessor));
        }

        return predecessors;
    }

    private int getEarlyStartFromGraph(Graph<VisualizationEngine.ActivityData> activityGraph, Activity activity) {
        Node node = activityNodes.get(activity);
        return activityGraph.getEdges().stream()
                .filter(e -> e.getEnd().equals(node))
                .mapToInt(e -> {
                    Activity prereq = nodeActivities.get(e.getStart());
                    return getEarlyStartFromGraph(activityGraph, prereq) + prereq.getDuration();
                })
                .max()
                .orElse(0);
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

        private Graph<VisualizationEngine.ActivityData> getActivityGraph() {
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
                project.getUtilityData().forEach(a -> a.getPrerequisites().forEach(p -> addEdge(p, a)));

                // Add edges for the resource dependencies
                project.getResources().forEach(r -> project.getActivityAssignments().entrySet().stream()
                        .filter(e -> e.getValue().contains(r))
                        .map(Map.Entry::getKey)
                        .sorted(comparing(a -> getEarlyStartFromGraph(activityGraph, a)))
                        .reduce(null, (p, a) -> {
                            if (p != null) {
                                addEdge(p, a);
                            }
                            return a;
                        })
                );

                project.getResourceTypes().forEach((t, r) -> {
                    SpanSet<VisualizationEngine.ActivityData> occupiedSpans = getResourceTypeOccupiedSpans(t);
                    List<Activity> unassignedActivities = project.getUtilityData().stream()
                            .filter(a -> t.equals(project.getActivityTypes().get(a)))
                            .filter(a -> project.getActivityAssignments().get(a) == null)
                            .collect(toList());

                    for (Activity unassignedActivity : unassignedActivities) {
                        int earlyStart = getEarlyStartFromGraph(activityGraph, unassignedActivity);
                        occupiedSpans.getSpans().stream()
                                .filter(s -> s.getStart() <= earlyStart)
                                .filter(s -> s.getEnd() >= earlyStart)
                                .findFirst()
                                .ifPresent(s -> addEdge(s.getEndActivity().getActivity(), unassignedActivity));
                    }
                });
            }

            return activityGraph;
        }

    }

}
