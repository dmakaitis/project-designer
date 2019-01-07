package com.portkullis.projectdesigner.adapter;

import com.portkullis.projectdesigner.engine.impl.Edge;
import com.portkullis.projectdesigner.engine.impl.Graph;
import com.portkullis.projectdesigner.engine.impl.Node;
import com.portkullis.projectdesigner.model.*;

import java.util.*;

import static com.portkullis.projectdesigner.model.SpanSet.asIntersection;
import static com.portkullis.projectdesigner.model.SpanSet.asUnion;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

/**
 * Common base class for project data adapters.
 *
 * @author darius
 */
abstract class AbstractProjectDataAdapter {

    protected final Project<Activity, String> project;

    private final Map<Activity, Node> activityNodes = new HashMap<>();
    private final Map<Node, Activity> nodeActivities = new HashMap<>();

    private Graph<?> activityGraph = null;
    private final Map<Activity, Integer> activityEarlyStarts = new HashMap<>();
    private final Map<Activity, Integer> activityLateStarts = new HashMap<>();

    AbstractProjectDataAdapter(Project<Activity, String> project) {
        this.project = project;
    }

    void clearCaches() {
        activityGraph = null;
        activityEarlyStarts.clear();
        activityLateStarts.clear();
    }

    Plan<Activity, String> getActivePlan() {
        return project.getPlans().computeIfAbsent(project.getActivePlan(), p -> new Plan<>());
    }

    private Graph<?> getActivityGraph() {
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
            getActivePlan().getResources().forEach(r -> getActivePlan().getActivityAssignments().entrySet().stream()
                    .filter(e -> e.getValue().contains(r))
                    .map(Map.Entry::getKey)
                    .sorted(comparing(this::getEarlyStartFromGraph))
                    .reduce(null, (p, a) -> {
                        if (p != null) {
                            addEdge(p, a);
                        }
                        return a;
                    })
            );

            getActivePlan().getResourceTypes().forEach((t, r) -> {
                SpanSet<Activity> occupiedSpans = getResourceTypeOccupiedSpans(t);
                List<Activity> unassignedActivities = project.getUtilityData().stream()
                        .filter(a -> t.equals(project.getActivityTypes().get(a)))
                        .filter(a -> getActivePlan().getActivityAssignments().get(a) == null)
                        .collect(toList());

                for (Activity unassignedActivity : unassignedActivities) {
                    int earlyStart = getEarlyStartFromGraph(unassignedActivity);
                    occupiedSpans.getSpans().stream()
                            .filter(s -> s.getStart() <= earlyStart)
                            .filter(s -> s.getEnd() >= earlyStart)
                            .findFirst()
                            .ifPresent(s -> addEdge(s.getEndActivity(), unassignedActivity));
                }
            });
        }

        return activityGraph;
    }

    private void addEdge(Activity a, Activity b) {
        Node nodeA = activityNodes.get(a);
        Node nodeB = activityNodes.get(b);

        if (!getAllPredecessors(nodeA).contains(nodeB)) {
            activityGraph.getEdges().add(new Edge<>(nodeA, nodeB));
        }

        activityEarlyStarts.clear();
        activityLateStarts.clear();
    }

    private Set<Node> getAllPredecessors(Node node) {
        Set<Node> directPredecessors = getActivityGraph().getEdges().stream()
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

    int getEarlyStartFromGraph(Activity activity) {
        return activityEarlyStarts.computeIfAbsent(activity, a -> {
            // Initialize the activity graph...
            getActivityGraph();

            Node node = activityNodes.get(a);

            return getActivityGraph().getEdges().stream()
                    .filter(e -> e.getEnd().equals(node))
                    .mapToInt(e -> {
                        Activity prereq = nodeActivities.get(e.getStart());
                        return getEarlyStartFromGraph(prereq) + prereq.getDuration();
                    })
                    .max()
                    .orElse(0);
        });
    }

    int getLateStartFromGraph(Activity activity) {
        return activityLateStarts.computeIfAbsent(activity, a -> getSuccessors(activity).stream()
                .mapToInt(s -> getEarlyStartFromGraph(s) - activity.getDuration())
                .min()
                .orElse(getEarlyStartFromGraph(activity)));
    }

    Collection<Activity> getPrerequisites(Activity activity) {
        // Initialize the activity graph...
        getActivityGraph();

        Node activityNode = activityNodes.get(activity);

        return getActivityGraph().getEdges().stream()
                .filter(e -> e.getEnd().equals(activityNode))
                .map(Edge::getStart)
                .map(nodeActivities::get)
                .collect(toSet());
    }

    Collection<Activity> getSuccessors(Activity activity) {
        // Initialize the activity graph...
        getActivityGraph();

        Node activityNode = activityNodes.get(activity);

        return getActivityGraph().getEdges().stream()
                .filter(e -> e.getStart().equals(activityNode))
                .map(Edge::getEnd)
                .map(nodeActivities::get)
                .collect(toSet());
    }

    private SpanSet<Activity> getResourceTypeOccupiedSpans(String resourceType) {
        SortedSet<String> resources = getActivePlan().getResourceTypes().get(resourceType);
        Map<String, SpanSet<Activity>> resourceSpans = resources.stream().collect(toMap(identity(), r -> getActivePlan().getActivityAssignments().entrySet().stream()
                .filter(e -> e.getValue().contains(r))  // NOSONAR - This really is the correct type, trust me... ;-)
                .map(Map.Entry::getKey)
                .sorted(comparingInt(this::getEarlyStartFromGraph).thenComparingInt(this::getLateStartFromGraph))
                .map(a -> new Span<>(getEarlyStartFromGraph(a), getEarlyStartFromGraph(a) + a.getDuration(), a))
                .collect(asUnion())
        ));

        return resourceSpans.values().stream()
                .collect(asIntersection());
    }

}
