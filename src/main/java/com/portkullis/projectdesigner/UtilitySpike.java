package com.portkullis.projectdesigner;

import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.engine.impl.VisualizationEngineImpl;
import com.portkullis.projectdesigner.model.*;

import java.util.*;

import static java.util.stream.Collectors.toSet;

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

            graph.simplifyDummies();
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

                Set<List<Edge<Activity>>> allDistinctPaths = graph.getAllDistinctPaths(s, e);
                System.out.println(allDistinctPaths.size() + " distinct paths through project network");

                System.out.println("Total nodes: " + graph.getNodes().size());
                System.out.println("Total activities: " + graph.getEdges().stream().filter(x -> x.getData() != null).count());
                System.out.println("Total dummies: " + graph.getEdges().stream().filter(x -> x.getData() == null).count());
                System.out.println("Total edges: " + graph.getEdges().size());
            }
        }

        visualizationEngine.visualizeGraph(graph);
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

}
