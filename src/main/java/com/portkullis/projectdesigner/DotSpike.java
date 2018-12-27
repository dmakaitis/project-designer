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

        graph.getEdges().add(new Edge<>(start, nodeA, "1"));
        graph.getEdges().add(new Edge<>(nodeA, nodeB, "2"));
        graph.getEdges().add(new Edge<>(nodeB, nodeC, "3"));
        graph.getEdges().add(new Edge<>(nodeC, nodeJ, "4"));
        graph.getEdges().add(new Edge<>(nodeJ, nodeR, "5"));
        graph.getEdges().add(new Edge<>(nodeC, nodeG, "6"));
        graph.getEdges().add(new Edge<>(nodeC, nodeD, "7"));
        graph.getEdges().add(new Edge<>(nodeC, nodeE, "8"));
        graph.getEdges().add(new Edge<>(nodeC, nodeH, "9"));
        graph.getEdges().add(new Edge<>(nodeC, nodeF, "10"));
        graph.getEdges().add(new Edge<>(nodeH, nodeN, "11"));
        graph.getEdges().add(new Edge<>(nodeF, nodeK, "12"));
        graph.getEdges().add(new Edge<>(nodeG, nodeK, "13"));
        graph.getEdges().add(new Edge<>(nodeK, nodeN, "14"));
        graph.getEdges().add(new Edge<>(nodeK, nodeL, "15"));
        graph.getEdges().add(new Edge<>(nodeG, nodeM, "16"));
        graph.getEdges().add(new Edge<>(nodeN, nodeQ, "17"));
        graph.getEdges().add(new Edge<>(nodeM, nodeP, "18"));
        graph.getEdges().add(new Edge<>(nodeP, nodeR, "19"));
        graph.getEdges().add(new Edge<>(nodeQ, nodeR, "20"));
        graph.getEdges().add(new Edge<>(nodeR, end, "21"));

        graph.getEdges().add(new Edge<>(nodeD, nodeE));
        graph.getEdges().add(new Edge<>(nodeE, nodeM));
        graph.getEdges().add(new Edge<>(nodeE, nodeN));
        graph.getEdges().add(new Edge<>(nodeG, nodeF));
        graph.getEdges().add(new Edge<>(nodeG, nodeH));
        graph.getEdges().add(new Edge<>(nodeL, nodeM));
        graph.getEdges().add(new Edge<>(nodeL, nodeN));
        graph.getEdges().add(new Edge<>(nodeQ, nodeP));

        visualizationEngine.visualizeGraph(graph);
    }

    private static Node addNode(Graph<String> graph, long nodeId, String label) {
        Node node = new Node(nodeId, label);
        graph.getNodes().add(node);
        return node;
    }

}
