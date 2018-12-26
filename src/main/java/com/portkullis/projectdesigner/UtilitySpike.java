package com.portkullis.projectdesigner;

import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.engine.impl.VisualizationEngineImpl;
import com.portkullis.projectdesigner.model.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class UtilitySpike {

    private static final VisualizationEngine<Activity> visualizationEngine = new VisualizationEngineImpl<>(a -> new EdgeProperties(Long.toString(a.getId())));

    public static void main(String[] args) {
        List<Activity> utilityData = new ArrayList<>();

        Activity activity1 = addActivity(utilityData, new Activity(1, "Requirements", 15));
        Activity activity2 = addActivity(utilityData, new Activity(2, "Architecture", 20, activity1));
        Activity activity3 = addActivity(utilityData, new Activity(3, "Project Planning", 20, activity2));
        Activity activity4 = addActivity(utilityData, new Activity(4, "Test Plan", 30, activity3));
        Activity activity5 = addActivity(utilityData, new Activity(5, "Test Harness", 33, activity4));
        Activity activity6 = addActivity(utilityData, new Activity(6, "Logging", 15, activity3));
        Activity activity7 = addActivity(utilityData, new Activity(7, "Security", 20, activity3));
        Activity activity8 = addActivity(utilityData, new Activity(8, "Pub/Sub", 5, activity3));
        Activity activity9 = addActivity(utilityData, new Activity(9, "Resource A", 20, activity3));
        Activity activity10 = addActivity(utilityData, new Activity(10, "Resource B", 15, activity3));
        Activity activity11 = addActivity(utilityData, new Activity(11, "Resource Access A", 10, activity6, activity9));
        Activity activity12 = addActivity(utilityData, new Activity(12, "Resource Access B", 5, activity6, activity10));
        Activity activity13 = addActivity(utilityData, new Activity(13, "Resource Access C", 16, activity6));
        Activity activity14 = addActivity(utilityData, new Activity(14, "Engine A", 20, activity12, activity13));
        Activity activity15 = addActivity(utilityData, new Activity(15, "Engine B", 25, activity12, activity13));
        Activity activity16 = addActivity(utilityData, new Activity(16, "Engine C", 15, activity6));
        Activity activity17 = addActivity(utilityData, new Activity(17, "Manager A", 20, activity7, activity8, activity11, activity14, activity15));
        Activity activity18 = addActivity(utilityData, new Activity(18, "Manager B", 25, activity7, activity8, activity15, activity16));
        Activity activity19 = addActivity(utilityData, new Activity(19, "Client App 1", 25, activity17, activity18));
        Activity activity20 = addActivity(utilityData, new Activity(20, "Client App 2", 35, activity17));
        Activity activity21 = addActivity(utilityData, new Activity(21, "System Test", 30, activity5, activity19, activity20));

        Graph<Activity> graph = new Graph();
        Date timerStart = new Date();
        try {
            long nodeId = 0;
            long edgeId = 0;

            Node start = new Node(++nodeId, "Start");
            Node end = new Node(++nodeId, "End");

            graph.getNodes().add(start);
            graph.getNodes().add(end);

            Map<Long, Node> activityEndNodeMap = new HashMap<>();

            for (Activity a : utilityData) {
                Node from = new Node(++nodeId, "N" + nodeId);
                Node to = new Node(++nodeId, "N" + nodeId);

                activityEndNodeMap.put(a.getId(), to);

                graph.getNodes().add(from);
                graph.getNodes().add(to);

                graph.getEdges().add(new Edge<>(++edgeId, start, from));
                graph.getEdges().add(new Edge<>(++edgeId, from, to, a));
                graph.getEdges().add(new Edge<>(++edgeId, to, end));

                for (Activity p : a.getPredecessors()) {
                    graph.getEdges().add(new Edge<>(++edgeId, activityEndNodeMap.get(p.getId()), from));
                }
            }

            collapseUnnecessaryDummies(graph);
        } finally {
            Date timerStop = new Date();
            System.out.println("Graph calculated in " + (timerStop.getTime() - timerStart.getTime()) + "ms");

            Set<Node> starts = getStartNodes(graph);
            Set<Node> ends = getEndNode(graph);

            System.out.println("Graph has " + starts.size() + " starts and " + ends.size() + " ends.");
            if (starts.size() == 1 && ends.size() == 1) {
                Node s = starts.stream().findFirst().get();
                Node e = ends.stream().findFirst().get();

                s.setLabel("Start");
                e.setLabel("End");

                System.out.println("Starting node: " + s);
                System.out.println("Ending node: " + e);

                Set<List<Edge<Activity>>> allDistinctPaths = getAllDistinctPaths(graph, s, e);
                System.out.println(allDistinctPaths.size() + " distinct paths through project network");

                System.out.println("Total nodes: " + graph.getNodes().size());
                System.out.println("Total activities: " + graph.getEdges().stream().filter(x -> x.getData() != null).count());
                System.out.println("Total dummies: " + graph.getEdges().stream().filter(x -> x.getData() == null).count());
                System.out.println("Total edges: " + graph.getEdges().size());
            }
        }

        visualizationEngine.visualizeGraph(graph);
    }

    private static void collapseUnnecessaryDummies(Graph<Activity> graph) {
        boolean keepSearching = true;

        findAndDeleteRedundantDummies(graph);
        mergeNodesWithIdenticalPrerequisites(graph);

        while (keepSearching) {
            Optional<Edge<Activity>> unnecessaryDummy = getUnnecessaryDummies(graph).findFirst();

            if (unnecessaryDummy.isPresent()) {
                collapseEdge(graph, unnecessaryDummy.get());
            } else {
                keepSearching = false;
            }
        }
    }

    private static <T> void mergeNodesWithIdenticalPrerequisites(Graph<T> graph) {
        Map<Node, Set<Long>> nodePrerequisites = graph.getNodes().stream()
                .collect(toMap(Function.identity(), n -> getPrerequisiteActivities(graph, n)));

        nodePrerequisites.forEach((k, v) -> {
            System.out.println("Node " + k.getLabel() + " => " + v);
        });

        List<Node> nodes = graph.getNodes().stream().sorted().collect(toList());

        while (nodes.size() > 1) {
            Node node = nodes.remove(0);

            for (Node compare : nodes) {
                if (nodePrerequisites.get(node).equals(nodePrerequisites.get(compare))) {
                    System.out.println("Merging node " + node + " into node " + compare + "...");
                    mergeNodes(graph, node, compare);
                }
            }
        }
    }

    private static <T> void mergeNodes(Graph<T> graph, Node node, Node target) {
        List<Edge> edgesToRemove = new ArrayList<>();

        Map<Node, Set<Node>> updatedEdges = new HashMap<>();

        graph.getEdges().forEach(edge -> {
            if (edge.getStart().equals(node)) {
                edge.setStart(target);
            }
            if (edge.getEnd().equals(node)) {
                edge.setEnd(target);
            }

            if (!updatedEdges.containsKey(edge.getStart())) {
                updatedEdges.put(edge.getStart(), new HashSet<>());
            }
            if (!updatedEdges.get(edge.getStart()).contains(edge.getEnd())) {
                updatedEdges.get(edge.getStart()).add(edge.getEnd());

                if (edge.getStart().equals(edge.getEnd())) {
                    edgesToRemove.add(edge);
                }
            } else {
                edgesToRemove.add(edge);
            }
        });

        graph.getEdges().removeAll(edgesToRemove);

        graph.getNodes().clear();
        graph.getNodes().addAll(graph.getEdges().stream()
                .flatMap(e -> Stream.of(e.getStart(), e.getEnd()))
                .collect(toSet())
        );
    }

    private static <T> Set<Long> getNodesThatAreConnectedTo(Graph<T> graph, Node node) {
        Set<Long> connected = graph.getEdges().stream()
                .filter(e -> e.getEnd().getId() == node.getId())
                .map(e -> e.getStart().getId())
                .collect(toSet());
        connected.addAll(graph.getEdges().stream()
                .filter(e -> e.getStart().getId() == node.getId())
                .map(e -> e.getEnd().getId())
                .collect(toSet()));

        return connected;
    }

    private static <T> Stream<Edge<T>> getUnnecessaryDummies(Graph<T> graph) {
        return graph.getEdges().stream()
                // Only dummy edges can be collapsed
                .filter(UtilitySpike::isDummyActivity)
                // Triangles can not be collapsed
                .filter(e -> isNotTriangleActivity(graph, e))
                // Edges can not be collapsed if the starting node has non-dummy edges leaving it and the end node has different prerequisite non-dummy activities
                .filter(e -> endDoesNotHaveAdditionalPrerequisites(graph, e))
                ;
    }

    private static <T> boolean endDoesNotHaveAdditionalPrerequisites(Graph<T> graph, Edge<T> e) {
        boolean allowed = true;
        if (graph.getEdges().stream()
                .filter(x -> x.getStart().getId() == e.getStart().getId())
                .count() > 1) {
            Set<Long> startPrerequisites = getPrerequisiteActivities(graph, e.getStart());
            Set<Long> endPrerequisites = getPrerequisiteActivities(graph, e.getEnd());

            endPrerequisites.removeAll(startPrerequisites);

            allowed = endPrerequisites.isEmpty();
        }
        return allowed;
    }

    private static <T> boolean isNotTriangleActivity(Graph<T> graph, Edge<T> e) {
        return Collections.disjoint(getNodesThatAreConnectedTo(graph, e.getStart()), getNodesThatAreConnectedTo(graph, e.getEnd()));
    }

    private static boolean isDummyActivity(Edge<?> e) {
        return e.getData() == null;
    }

    private static <T> Set<Long> getPrerequisiteActivities(Graph<T> graph, Node end) {
        Set<Node> startNodes = getStartNodes(graph);

        if (startNodes.size() == 0) {
            throw new RuntimeException("Could not find starting node for graph.");
        }

        if (startNodes.size() > 1) {
            throw new RuntimeException("Multiple start nodes found for graph.");
        }

        return getAllDistinctPaths(graph, startNodes.stream().findFirst().get(), end).stream()
                .flatMap(List::stream)
                .filter(e -> e.getData() != null)
                .map(Edge::getId)
                .collect(toSet());
    }

    private static <T> Set<List<Edge<T>>> getAllDistinctPaths(Graph<T> graph, Node start, Node end) {
        return getAllDistinctPaths(graph, new ArrayList<>(), start, end);
    }

    private static <T> Set<List<Edge<T>>> getAllDistinctPaths(Graph<T> graph, List<Edge<T>> basePath, Node start, Node end) {
        if (!graph.getNodes().contains(start)) {
            throw new RuntimeException("Graph does not contain starting node: " + start);
        }
        if (!graph.getNodes().contains(end)) {
            throw new RuntimeException("Graph does not contain ending node: " + end);
        }

        Set<List<Edge<T>>> results = new HashSet<>();

        if (!start.equals(end)) {
            Set<Edge<T>> nextEdges = graph.getEdges().stream()
                    .filter(e -> e.getStart().equals(start))
                    .collect(toSet());

            for (Edge<T> nextEdge : nextEdges) {
                List<Edge<T>> newBasePath = new ArrayList<>(basePath);
                newBasePath.add(nextEdge);
                if (nextEdge.getEnd().equals(end)) {
                    results.add(newBasePath);
                } else {
                    results.addAll(getAllDistinctPaths(graph, newBasePath, nextEdge.getEnd(), end));
                }
            }

        }

        return results;
    }

    private static void findAndDeleteRedundantDummies(Graph<Activity> graph) {
        List<Edge> dummies = graph.getEdges().stream().filter(e -> e.getData() == null).collect(toList());

        for (Edge dummy : dummies) {
            if (countPaths(graph, dummy.getStart(), dummy.getEnd()) > 1) {
                graph.getEdges().remove(dummy);
            }
        }
    }

    private static <T> int countPaths(Graph<T> graph, Node start, Node end) {
        return getAllDistinctPaths(graph, start, end).size();
    }

    private static <T> Set<Node> getStartNodes(Graph<T> graph) {
        Set<Long> nodes = graph.getEdges().stream()
                .map(e -> e.getEnd().getId())
                .collect(toSet());
        return graph.getNodes().stream()
                .filter(n -> !nodes.contains(n.getId()))
                .collect(toSet());
    }

    private static Set<Node> getEndNode(Graph<?> graph) {
        Set<Long> nodes = graph.getEdges().stream()
                .map(e -> e.getStart().getId())
                .collect(toSet());
        return graph.getNodes().stream()
                .filter(n -> !nodes.contains(n.getId()))
                .collect(toSet());
    }

    private static Activity addActivity(List<Activity> utilityData, Activity activity) {
        utilityData.add(activity);
        return activity;
    }

    private static <T> boolean collapseEdge(Graph<T> graph, Edge<T> edge) {
        boolean edgeCollapsed = graph.getEdges().removeIf(e -> e.getId() == edge.getId());

        if (edgeCollapsed) {
            if (graph.getEdges().stream().filter(e -> e.getId() == edge.getId()).count() > 0) {
                throw new RuntimeException("Edge wasn't really removed: " + edge);
            }

            Node a = edge.getStart();
            Node b = edge.getEnd();

            for (Edge<T> e : graph.getEdges()) {
                if (e.getStart().equals(b)) {
                    e.setStart(a);
                }
                if (e.getEnd().equals(b)) {
                    e.setEnd(a);
                }
            }
        }

        graph.getNodes().clear();
        graph.getNodes().addAll(graph.getEdges().stream()
                .flatMap(e -> Stream.of(e.getStart(), e.getEnd()))
                .collect(toSet())
        );

        return edgeCollapsed;
    }

}
