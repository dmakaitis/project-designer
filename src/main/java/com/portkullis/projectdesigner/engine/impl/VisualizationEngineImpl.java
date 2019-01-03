package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.exception.ProjectDesignerRuntimeException;
import com.portkullis.projectdesigner.model.EdgeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Implementation of a project visualization engine.
 */
public class VisualizationEngineImpl implements VisualizationEngine {

    private static final Logger LOG = LoggerFactory.getLogger(VisualizationEngine.class);

    @Override
    public void visualizeProject(ProjectData project) {
        Graph<ActivityData> graph = new Graph<>();

        IdGenerator nodeIdGenerator = new IdGenerator();

        Map<ActivityData, Node> activityStartNodeMap = new HashMap<>();
        Map<ActivityData, Node> activityEndNodeMap = new HashMap<>();

        Node start = createGraphNode(graph, nodeIdGenerator, "Start");

        createActivityEdges(project, graph, nodeIdGenerator, activityStartNodeMap, activityEndNodeMap);

        createDependencyEdges(project, graph, start, activityStartNodeMap, activityEndNodeMap);

        createProjectFinishEdges(graph, nodeIdGenerator);

        graph.simplifyDummies();

        labelNodes(graph);

        visualizeGraph(graph);
    }

    private static Node createGraphNode(Graph<ActivityData> graph, IdGenerator nodeIdGenerator, String start2) {
        Node start = new Node(nodeIdGenerator.getNextId(), start2);
        graph.getNodes().add(start);
        return start;
    }

    private static void createActivityEdges(ProjectData project, Graph<ActivityData> graph, IdGenerator nodeIdGenerator, Map<ActivityData, Node> activityStartNodeMap, Map<ActivityData, Node> activityEndNodeMap) {
        for (ActivityData activity : project.getActivities()) {
            Node from = new Node(nodeIdGenerator.getNextId(), "N" + nodeIdGenerator.getNextId());
            Node to = new Node(nodeIdGenerator.getNextId(), "N" + nodeIdGenerator.getNextId());

            activityStartNodeMap.put(activity, from);
            activityEndNodeMap.put(activity, to);

            graph.getNodes().add(from);
            graph.getNodes().add(to);

            graph.getEdges().add(new Edge<>(from, to, activity));
        }
    }

    private static void createDependencyEdges(ProjectData project, Graph<ActivityData> graph, Node start, Map<ActivityData, Node> activityStartNodeMap, Map<ActivityData, Node> activityEndNodeMap) {
        for (ActivityData activity : project.getActivities()) {
            Node from = activityStartNodeMap.get(activity);

            Collection<ActivityData> prerequisites = activity.getPrerequisites();
            if (prerequisites.isEmpty()) {
                graph.getEdges().add(new Edge<>(start, from));
            } else {
                for (ActivityData p : prerequisites) {
                    graph.getEdges().add(new Edge<>(activityEndNodeMap.get(p), from));
                }
            }
        }
    }

    private static void createProjectFinishEdges(Graph<ActivityData> graph, IdGenerator nodeIdGenerator) {
        Node end = createGraphNode(graph, nodeIdGenerator, "End");
        for (Node terminalNode : graph.getTerminalNodes()) {
            if (!terminalNode.equals(end)) {
                graph.getEdges().add(new Edge<>(terminalNode, end));
            }
        }
    }

    private static void labelNodes(Graph<ActivityData> graph) {
        Node start;
        Set<Node> startNodes = graph.getStartNodes();
        if (startNodes.size() != 1) {
            throw new ProjectDesignerRuntimeException("A project must have exactly one starting activity. Project has " + startNodes.size() + " start activities.");
        }
        startNodes.forEach(n -> n.setLabel("Start"));
        start = startNodes.iterator().next();

        IdGenerator labelGenerator = new IdGenerator();
        Set<Long> labeledNodes = new HashSet<>();
        labeledNodes.add(start.getId());
        labelChildrenNodes(graph, start, labelGenerator, labeledNodes);

        Set<Node> terminalNodes = graph.getTerminalNodes();
        if (terminalNodes.size() != 1) {
            throw new ProjectDesignerRuntimeException("A project must have exactly one ending activity.");
        }
        terminalNodes.forEach(n -> n.setLabel("End"));
    }

    private static void labelChildrenNodes(Graph<ActivityData> graph, Node start, IdGenerator labelGenerator, Set<Long> labeledNodes) {
        List<Node> exits = graph.getEdges().stream()
                .filter(e -> e.getStart().equals(start))
                .map(Edge::getEnd)
                .collect(toList());

        exits.forEach(n -> {
            Set<Long> sources = graph.getEdges().stream()
                    .filter(e -> e.getEnd().equals(n))
                    .map(Edge::getStart)
                    .map(Node::getId)
                    .collect(toSet());
            if (labeledNodes.containsAll(sources) && !labeledNodes.contains(n.getId())) {
                n.setLabel(labelGenerator.getNextLabel());
                labeledNodes.add(n.getId());
            }
        });
        exits.forEach(n -> labelChildrenNodes(graph, n, labelGenerator, labeledNodes));
    }

    @Override
    public void visualizeGraph(Graph<ActivityData> graph) {
        FloatCalculator floatCalculator = new FloatCalculator(graph);
        Map<Node, Integer> nodeFloatMap = new HashMap<>();

        int maxTotalFloat = graph.getEdges().stream()
                .mapToInt(floatCalculator::getTotalFloat)
                .max()
                .orElse(0);
        int highThreshold = maxTotalFloat / 9;
        int medThreshold = maxTotalFloat / 3;

        try {
            StringBuilder buffer = new StringBuilder();
            buffer.append("digraph {\n");
            buffer.append("    rankdir = LR\n");
            graph.getEdges().forEach(e -> {
                int totalFloat = floatCalculator.getTotalFloat(e);

                nodeFloatMap.put(e.getStart(), Math.min(totalFloat, nodeFloatMap.getOrDefault(e.getStart(), Integer.MAX_VALUE)));
                nodeFloatMap.put(e.getEnd(), Math.min(totalFloat, nodeFloatMap.getOrDefault(e.getEnd(), Integer.MAX_VALUE)));

                buffer.append("    ").append(e.getStart().getLabel()).append(" -> ").append(e.getEnd().getLabel());
                if (e.getData().isPresent()) {
                    EdgeProperties ep = e.getData().get().getEdgeProperties();
                    buffer.append(" [ label = \"")
                            .append(ep.getLabel())
                            .append("\"; ");
                } else {
                    buffer.append(" [ style = dotted; ");
                }

                if (totalFloat == 0) {
                    buffer.append(" color = black; penwidth = 2; ]");
                } else if (totalFloat <= highThreshold) {
                    buffer.append(" color = red; ]");
                } else if (totalFloat <= medThreshold) {
                    buffer.append(" color = orange; ]");
                } else {
                    buffer.append(" color = green; ]");
                }
                buffer.append(";\n");
            });

            graph.getNodes().forEach(n -> {
                int totalFloat = nodeFloatMap.getOrDefault(n, 0);
                buffer.append("    ").append(n.getLabel());
                if (totalFloat == 0) {
                    buffer.append(" [ color = black; penwidth = 2; ]");
                } else if (totalFloat <= highThreshold) {
                    buffer.append(" [ color = red; ]");
                } else if (totalFloat <= medThreshold) {
                    buffer.append(" [ color = orange; ]");
                } else {
                    buffer.append(" [ color = green; ]");
                }
                buffer.append(";\n");
            });
            buffer.append("}");

            Process dot = Runtime.getRuntime().exec("dot -Tpdf -o output.pdf");
            dot.getOutputStream().write(buffer.toString().getBytes());
            dot.getOutputStream().close();
            dot.waitFor();
        } catch (Exception e) {
            LOG.error("Failed to create graph visualization", e);
        }
    }

}
