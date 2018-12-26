package com.portkullis.projectdesigner;

import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.engine.impl.VisualizationEngineImpl;
import com.portkullis.projectdesigner.model.*;

import java.util.*;

import static java.util.stream.Collectors.toList;

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
            System.out.println(utilityData);

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

            findAndDeleteRedundantDummies(graph);
            collapseUnnecessaryDummies(graph);
//            collapseEdge(graph, getEdge(graph, 1, 3));
//            collapseEdge(graph, getEdge(graph, 4, 5));
//            collapseEdge(graph, getEdge(graph, 6, 7));
//            collapseEdge(graph, getEdge(graph, 8, 9));
//            collapseEdge(graph, getEdge(graph, 8, 15));
//            collapseEdge(graph, getEdge(graph, 8, 13));
//            collapseEdge(graph, getEdge(graph, 8, 17));
//            collapseEdge(graph, getEdge(graph, 8, 18));
//            collapseEdge(graph, getEdge(graph, 8, 19));

            int pathCount = countPaths(graph, start, end);
            System.out.println(pathCount + " paths from start to end");
        } finally {
            Date timerStop = new Date();
            System.out.println("Graph calculated in " + (timerStop.getTime() - timerStart.getTime()) + "ms");
        }

        visualizationEngine.visualizeGraph(graph);
    }

    private static <T> Edge<T> getEdge(Graph<T> graph, long nodeA, long nodeB) {
        return graph.getEdges().stream().filter(e -> e.getStart().getId() == nodeA && e.getEnd().getId() == nodeB).findFirst().get();
    }

    private static void findAndDeleteRedundantDummies(Graph<Activity> graph) {
        List<Edge> dummies = graph.getEdges().stream().filter(e -> e.getData() == null).collect(toList());

        for (Edge dummy : dummies) {
            if (countPaths(graph, dummy.getStart(), dummy.getEnd()) > 1) {
                graph.getEdges().remove(dummy);
            }
        }
    }

    private static int countPaths(Graph<Activity> graph, Node start, Node end) {
        int count = 0;
        Collection<Edge> edges = getEdgesThatStartWith(start, graph);

        for (Edge edge : edges) {
            if (edge.getEnd().equals(end)) {
                count++;
            } else {
                count += countPaths(graph, edge.getEnd(), end);
            }
        }

        return count;
    }

    private static Collection<Edge> getEdgesThatStartWith(Node start, Graph<Activity> graph) {
        return graph.getEdges().stream().filter(e -> e.getStart().equals(start)).collect(toList());
    }

    private static Activity addActivity(List<Activity> utilityData, Activity activity) {
        utilityData.add(activity);
        return activity;
    }

    private static <T> void collapseUnnecessaryDummies(Graph<T> graph) {
        boolean pruning = true;

        while (pruning) {
            pruning = false;
            List<Edge<T>> dummies = graph.getEdges().stream().filter(e -> e.getData() == null).collect(toList());

            for (Edge<T> dummy : dummies) {
//                long startInputs = graph.getEdges().stream().filter(e -> e.getEnd().equals(dummy.getStart())).count();
                long endInputs = graph.getEdges().stream().filter(e -> e.getEnd().equals(dummy.getEnd())).count();
                long endOutputs = graph.getEdges().stream().filter(e -> e.getStart().equals(dummy.getEnd())).count();

                if ((endInputs <= 1) && (endOutputs <= 1)) {
                    System.out.println("Collapsing edge from " + dummy.getStart().getLabel() + " to " + dummy.getEnd().getLabel());
                    pruning = collapseEdge(graph, dummy);
                    break;
                }
            }
        }
    }

    private static <T> boolean collapseEdge(Graph<T> graph, Edge<T> edge) {
        boolean edgeCollapsed = graph.getEdges().removeIf(e -> e.getId() == edge.getId());

        if (edgeCollapsed) {
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

            graph.getNodes().remove(b);
        }

        return edgeCollapsed;
    }

}
