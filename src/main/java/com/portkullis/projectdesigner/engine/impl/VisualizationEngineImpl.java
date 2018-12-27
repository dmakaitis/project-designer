package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.exception.ProjectDesignerRuntimeException;
import com.portkullis.projectdesigner.model.EdgeProperties;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class VisualizationEngineImpl<T, I> implements VisualizationEngine<T> {

    private final Function<T, I> edgeIdentityMapper;
    private final Function<T, Collection<T>> edgePrerequisiteMapper;
    private final Function<T, EdgeProperties> edgePropertyMapper;

    public VisualizationEngineImpl(Function<T, I> edgeIdentityMapper, Function<T, Collection<T>> edgePrerequisiteMapper, Function<T, EdgeProperties> edgePropertyMapper) {
        this.edgeIdentityMapper = edgeIdentityMapper;
        this.edgePrerequisiteMapper = edgePrerequisiteMapper;
        this.edgePropertyMapper = edgePropertyMapper;
    }

    @Override
    public void visualizeUtilityData(Collection<T> utilityData) {
        Graph<T> graph = new Graph<>();

        IdGenerator nodeIdGenerator = new IdGenerator();
        IdGenerator edgeIdGenerator = new IdGenerator();

        Node start = new Node(nodeIdGenerator.getNextId(), "Start");
        Node end = new Node(nodeIdGenerator.getNextId(), "End");

        Map<I, Node> activityEndNodeMap = new HashMap<>();

        for (T activity : utilityData) {
            long nodeId = nodeIdGenerator.getNextId();
            Node from = new Node(nodeId, "N" + nodeId);
            nodeId = nodeIdGenerator.getNextId();
            Node to = new Node(nodeId, "N" + nodeId);

            activityEndNodeMap.put(edgeIdentityMapper.apply(activity), to);

            graph.getNodes().add(from);
            graph.getNodes().add(to);

            graph.getEdges().add(new Edge<>(edgeIdGenerator.getNextId(), start, from));
            graph.getEdges().add(new Edge<>(edgeIdGenerator.getNextId(), from, to, activity));
            graph.getEdges().add(new Edge<>(edgeIdGenerator.getNextId(), to, end));

            for (T p : edgePrerequisiteMapper.apply(activity)) {
                graph.getEdges().add(new Edge<>(edgeIdGenerator.getNextId(), activityEndNodeMap.get(edgeIdentityMapper.apply(p)), from));
            }
        }

        graph.simplifyDummies();

        Set<Node> startNodes = graph.getStartNodes();
        if (startNodes.size() != 1) {
            throw new ProjectDesignerRuntimeException("A project must have exactly one starting activity.");
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

        visualizeGraph(graph);
    }

    private void labelChildrenNodes(Graph<T> graph, Node start, IdGenerator labelGenerator, Set<Long> labeledNodes) {
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
            if (labeledNodes.containsAll(sources)) {
                if (!labeledNodes.contains(n.getId())) {
                    n.setLabel(labelGenerator.getNextLabel());
                    labeledNodes.add(n.getId());
                }
            }
        });
        exits.forEach(n -> labelChildrenNodes(graph, n, labelGenerator, labeledNodes));
    }

    @Override
    public void visualizeGraph(Graph<T> graph) {
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("digraph {\n");
            buffer.append("    rankdir = LR\n");
            graph.getEdges().forEach(e -> {
                buffer.append("    ").append(e.getStart().getLabel()).append(" -> ").append(e.getEnd().getLabel());
                if (e.getData() != null) {
                    EdgeProperties ep = edgePropertyMapper.apply(e.getData());
                    buffer.append(" [ label = \"").append(ep.getLabel()).append("\" ]");
                } else {
                    buffer.append(" [ style = dotted ]");
                }
                buffer.append(";\n");
            });
            buffer.append("}");

            Process dot = Runtime.getRuntime().exec("dot -Tpdf -o output.pdf");
            dot.getOutputStream().write(buffer.toString().getBytes());
            dot.getOutputStream().close();
            dot.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
