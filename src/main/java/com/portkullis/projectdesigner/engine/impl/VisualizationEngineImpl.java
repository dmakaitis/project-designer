package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.exception.ProjectDesignerRuntimeException;
import com.portkullis.projectdesigner.model.EdgeProperties;
import com.portkullis.projectdesigner.model.Project;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class VisualizationEngineImpl<A, I> implements VisualizationEngine<A> {

    private final Function<A, I> edgeIdentityMapper;
    private final Function<A, Collection<A>> edgePrerequisiteMapper;
    private final Function<A, EdgeProperties> edgePropertyMapper;

    public VisualizationEngineImpl(Function<A, I> edgeIdentityMapper, Function<A, Collection<A>> edgePrerequisiteMapper, Function<A, EdgeProperties> edgePropertyMapper) {
        this.edgeIdentityMapper = edgeIdentityMapper;
        this.edgePrerequisiteMapper = edgePrerequisiteMapper;
        this.edgePropertyMapper = edgePropertyMapper;
    }

    @Override
    public void visualizeProject(Project<A> project) {
        Graph<A> graph = new Graph<>();

        IdGenerator nodeIdGenerator = new IdGenerator();

        Node start = new Node(nodeIdGenerator.getNextId(), "Start");
        graph.getNodes().add(start);

        Map<I, Node> activityEndNodeMap = new HashMap<>();

        for (A activity : project.getUtilityData()) {
            long nodeId = nodeIdGenerator.getNextId();
            Node from = new Node(nodeId, "N" + nodeId);
            nodeId = nodeIdGenerator.getNextId();
            Node to = new Node(nodeId, "N" + nodeId);

            activityEndNodeMap.put(edgeIdentityMapper.apply(activity), to);

            graph.getNodes().add(from);
            graph.getNodes().add(to);

            graph.getEdges().add(new Edge<>(from, to, activity));

            Collection<A> prerequisites = edgePrerequisiteMapper.apply(activity);
            if (prerequisites.isEmpty()) {
                graph.getEdges().add(new Edge<>(start, from));
            } else {
                for (A p : prerequisites) {
                    graph.getEdges().add(new Edge<>(activityEndNodeMap.get(edgeIdentityMapper.apply(p)), from));
                }
            }
        }

        Node end = new Node(nodeIdGenerator.getNextId(), "End");
        graph.getNodes().add(end);
        for (Node terminalNode : graph.getTerminalNodes()) {
            if (!terminalNode.equals(end)) {
                graph.getEdges().add(new Edge<>(terminalNode, end));
            }
        }

        graph.simplifyDummies();

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

        visualizeGraph(graph);
    }

    private void labelChildrenNodes(Graph<A> graph, Node start, IdGenerator labelGenerator, Set<Long> labeledNodes) {
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
    public void visualizeGraph(Graph<A> graph) {
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
