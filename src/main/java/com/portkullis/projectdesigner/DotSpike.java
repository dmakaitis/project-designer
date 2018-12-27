package com.portkullis.projectdesigner;

import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.engine.impl.Edge;
import com.portkullis.projectdesigner.engine.impl.Graph;
import com.portkullis.projectdesigner.engine.impl.Node;
import com.portkullis.projectdesigner.engine.impl.VisualizationEngineImpl;
import com.portkullis.projectdesigner.model.EdgeProperties;

import java.util.HashSet;

import static java.util.function.Function.identity;

public class DotSpike {

    private static final VisualizationEngine<String> visualizationEngine = new VisualizationEngineImpl<>(identity(), e -> new HashSet<>(), EdgeProperties::new);

    public static void main(String[] args) {
        Graph<String> graph = new Graph<>();

        long nodeId = 0;

        Node start = addNode(graph, ++nodeId, "Start");
        Node nodeA = addNode(graph, ++nodeId, "A");
        Node nodeB = addNode(graph, ++nodeId, "B");
        Node nodeC = addNode(graph, ++nodeId, "C");
        Node nodeD = addNode(graph, ++nodeId, "D");
        Node nodeE = addNode(graph, ++nodeId, "E");
        Node nodeF = addNode(graph, ++nodeId, "F");
        Node nodeG = addNode(graph, ++nodeId, "G");
        Node nodeH = addNode(graph, ++nodeId, "H");
        Node nodeJ = addNode(graph, ++nodeId, "J");
        Node nodeK = addNode(graph, ++nodeId, "K");
        Node nodeL = addNode(graph, ++nodeId, "L");
        Node nodeM = addNode(graph, ++nodeId, "M");
        Node nodeN = addNode(graph, ++nodeId, "N");
        Node nodeP = addNode(graph, ++nodeId, "P");
        Node nodeQ = addNode(graph, ++nodeId, "Q");
        Node nodeR = addNode(graph, ++nodeId, "R");
        Node end = addNode(graph, ++nodeId, "End");

        long edgeId = 0;

        graph.getEdges().add(new Edge<>(++edgeId, start, nodeA, "1"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeA, nodeB, "2"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeB, nodeC, "3"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeC, nodeJ, "4"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeJ, nodeR, "5"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeC, nodeG, "6"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeC, nodeD, "7"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeC, nodeE, "8"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeC, nodeH, "9"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeC, nodeF, "10"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeH, nodeN, "11"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeF, nodeK, "12"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeG, nodeK, "13"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeK, nodeN, "14"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeK, nodeL, "15"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeG, nodeM, "16"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeN, nodeQ, "17"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeM, nodeP, "18"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeP, nodeR, "19"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeQ, nodeR, "20"));
        graph.getEdges().add(new Edge<>(++edgeId, nodeR, end, "21"));

        graph.getEdges().add(new Edge<>(++edgeId, nodeD, nodeE));
        graph.getEdges().add(new Edge<>(++edgeId, nodeE, nodeM));
        graph.getEdges().add(new Edge<>(++edgeId, nodeE, nodeN));
        graph.getEdges().add(new Edge<>(++edgeId, nodeG, nodeF));
        graph.getEdges().add(new Edge<>(++edgeId, nodeG, nodeH));
        graph.getEdges().add(new Edge<>(++edgeId, nodeL, nodeM));
        graph.getEdges().add(new Edge<>(++edgeId, nodeL, nodeN));
        graph.getEdges().add(new Edge<>(++edgeId, nodeQ, nodeP));

        visualizationEngine.visualizeGraph(graph);
    }

    private static Node addNode(Graph<String> graph, long nodeId, String label) {
        Node node = new Node(nodeId, label);
        graph.getNodes().add(node);
        return node;
    }

}
