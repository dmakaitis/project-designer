package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.exception.ProjectDesignerRuntimeException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * A graph that represents a project plan.
 *
 * @param <T> the type of activity that is represented by each non-dummy edge in the graph.
 */
public class Graph<T> {

    private Set<Node> nodes;
    private Set<Edge<T>> edges;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Graph{");
        sb.append("nodes=").append(nodes);
        sb.append(", edges=").append(edges);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graph graph = (Graph) o;
        return Objects.equals(nodes, graph.nodes) &&
                Objects.equals(edges, graph.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, edges);
    }

    /**
     * Returns all the nodes in the graph.
     *
     * @return all the nodes in the graph.
     */
    public Set<Node> getNodes() {
        if (nodes == null) {
            nodes = new HashSet<>();
        }
        return nodes;
    }

    /**
     * Returns all of the edges in the graph.
     *
     * @return the edges in the graph.
     */
    public Set<Edge<T>> getEdges() {
        if (edges == null) {
            edges = new HashSet<>();
        }
        return edges;
    }

    private void fixNodes() {
        nodes = edges.stream()
                .flatMap(e -> Stream.of(e.getStart(), e.getEnd()))
                .collect(Collectors.toSet());
    }

    /**
     * Simplifies the graph by removing or merging dummy activities to arrive at a graph with a minimal number of
     * nodes and edges that is equivilant to the original graph.
     */
    public void simplifyDummies() {
        boolean keepSearching = true;

        fixNodes();

        findAndDeleteRedundantDummies();
        mergeNodesWithIdenticalPrerequisites();

        while (keepSearching) {
            simplifySimilarDummyNodes();
            collapseDummiesThatAreOnlyExitFromNodes();

            Optional<Edge<T>> unnecessaryDummy = getUnnecessaryDummies().findFirst();

            if (unnecessaryDummy.isPresent()) {
                collapseEdge(unnecessaryDummy.get());
            } else {
                keepSearching = false;
            }
        }
    }

    private void findAndDeleteRedundantDummies() {
        List<Edge> dummies = edges.stream().filter(e -> e.getData() == null).collect(toList());

        for (Edge dummy : dummies) {
            if (countPaths(dummy.getStart(), dummy.getEnd()) > 1) {
                edges.remove(dummy);
            }
        }

        fixNodes();
    }

    private int countPaths(Node start, Node end) {
        return getAllDistinctPaths(start, end).size();
    }

    /**
     * Returns a collection of all the distinct paths between the two nodes within this graph.
     *
     * @param start the starting node.
     * @param end   the ending node.
     * @return a collection of distinct paths between the two nodes.
     * @throws RuntimeException if either node does not exist in this graph.
     */
    public Set<List<Edge<T>>> getAllDistinctPaths(Node start, Node end) {
        return getAllDistinctPaths(new ArrayList<>(), start, end);
    }

    private Set<List<Edge<T>>> getAllDistinctPaths(List<Edge<T>> basePath, Node start, Node end) {
        if (!nodes.contains(start)) {
            throw new ProjectDesignerRuntimeException("Graph does not contain starting node: " + start);
        }
        if (!nodes.contains(end)) {
            throw new ProjectDesignerRuntimeException("Graph does not contain ending node: " + end);
        }

        Set<List<Edge<T>>> results = new HashSet<>();

        if (!start.equals(end)) {
            Set<Edge<T>> nextEdges = edges.stream()
                    .filter(e -> e.getStart().equals(start))
                    .collect(toSet());

            for (Edge<T> nextEdge : nextEdges) {
                List<Edge<T>> newBasePath = new ArrayList<>(basePath);
                newBasePath.add(nextEdge);
                if (nextEdge.getEnd().equals(end)) {
                    results.add(newBasePath);
                } else {
                    results.addAll(getAllDistinctPaths(newBasePath, nextEdge.getEnd(), end));
                }
            }

        }

        return results;
    }

    private void mergeNodesWithIdenticalPrerequisites() {
        Map<Node, Set<Edge<T>>> nodePrerequisites = nodes.stream()
                .collect(toMap(Function.identity(), this::getPrerequisiteActivities));

        List<Node> nodesToCompare = nodes.stream().sorted().collect(toList());

        while (nodesToCompare.size() > 1) {
            Node compareNode = nodesToCompare.remove(0);

            for (Node otherNode : nodesToCompare) {
                if (nodePrerequisites.get(compareNode).equals(nodePrerequisites.get(otherNode))) {
                    mergeNodes(compareNode, otherNode);
                }
            }
        }
    }

    void mergeNodes(Node node, Node target) {
        List<Edge<T>> edgesToUpdate = edges.stream()
                .filter(e -> e.getStart().equals(node) || e.getEnd().equals(node))
                .collect(toList());
        edges.removeAll(edgesToUpdate);

        edgesToUpdate.forEach(edge -> {
            Node start = edge.getStart().equals(node) ? target : edge.getStart();
            Node end = edge.getEnd().equals(node) ? target : edge.getEnd();

            if (!start.equals(end)) {
                edges.add(new Edge<>(start, end, edge.getData()));
            }
        });

        fixNodes();
    }

    private Set<Edge<T>> getPrerequisiteActivities(Node end) {
        Set<Node> startNodes = getStartNodes();

        if (startNodes.isEmpty()) {
            throw new ProjectDesignerRuntimeException("Could not find starting node for graph.");
        }

        if (startNodes.size() > 1) {
            throw new ProjectDesignerRuntimeException("Multiple start nodes found for graph.");
        }

        return getAllDistinctPaths(startNodes.stream().findFirst().get(), end).stream()
                .flatMap(List::stream)
                .filter(e -> e.getData() != null)
                .collect(toSet());
    }

    /**
     * Returns the set of nodes that have no edges leading into them.
     *
     * @return the set of starting nodes in the graph.
     */
    public Set<Node> getStartNodes() {
        Set<Node> nodesWithEdges = edges.stream()
                .map(e -> e.getEnd())
                .collect(toSet());
        return nodes.stream()
                .filter(n -> !nodesWithEdges.contains(n))
                .collect(toSet());
    }

    /**
     * Returns the set of nodes that have no edges leading out of them.
     *
     * @return the set of terminal nodes in the graph.
     */
    public Set<Node> getTerminalNodes() {
        Set<Node> nodesWithEdges = edges.stream()
                .map(e -> e.getStart())
                .collect(toSet());
        return nodes.stream()
                .filter(n -> !nodesWithEdges.contains(n))
                .collect(toSet());
    }

    private Stream<Edge<T>> getUnnecessaryDummies() {
        return edges.stream()
                // Only dummy edges can be collapsed
                .filter(this::isDummyActivity)
                // Triangles can not be collapsed
                .filter(this::isNotTriangleActivity)
                // Edges can not be collapsed if the starting node has non-dummy edges leaving it and the end node has different prerequisite non-dummy activities
                .filter(this::endDoesNotHaveAdditionalPrerequisites);
    }

    private boolean isDummyActivity(Edge<T> e) {
        return e.getData() == null;
    }

    private boolean isNotTriangleActivity(Edge<T> e) {
        return Collections.disjoint(getNodesThatAreConnectedTo(e.getStart()), getNodesThatAreConnectedTo(e.getEnd()));
    }

    private Set<Long> getNodesThatAreConnectedTo(Node node) {
        Set<Long> connected = edges.stream()
                .filter(e -> e.getEnd().getId() == node.getId())
                .map(e -> e.getStart().getId())
                .collect(toSet());
        connected.addAll(edges.stream()
                .filter(e -> e.getStart().getId() == node.getId())
                .map(e -> e.getEnd().getId())
                .collect(toSet()));

        return connected;
    }

    private boolean endDoesNotHaveAdditionalPrerequisites(Edge<T> e) {
        boolean allowed = true;
        if (edges.stream()
                .filter(x -> x.getStart().getId() == e.getStart().getId())
                .count() > 1) {
            Set<Edge<T>> startPrerequisites = getPrerequisiteActivities(e.getStart());
            Set<Edge<T>> endPrerequisites = getPrerequisiteActivities(e.getEnd());

            endPrerequisites.removeAll(startPrerequisites);

            allowed = endPrerequisites.isEmpty();
        }
        return allowed;
    }

    void collapseEdge(Edge<T> edge) {
        if (edge.getData() != null) {
            throw new ProjectDesignerRuntimeException("Can not collapse an edge with activity data: " + edge);
        }
        mergeNodes(edge.getStart(), edge.getEnd());
//        boolean edgeCollapsed = edges.remove(edge);
//
//        if (edgeCollapsed) {
//            Node start = edge.getStart();
//            Node end = edge.getEnd();
//
//            List<Edge<T>> edgesToUpdate = edges.stream()
//                    .filter(e -> e.getStart().equals(end) || e.getEnd().equals(end))
//                    .collect(toList());
//            edges.removeAll(edgesToUpdate);
//
//            edgesToUpdate.forEach(e -> {
//                Node newStart = e.getStart().equals(end) ? start : e.getStart();
//                Node newEnd = e.getEnd().equals(end) ? start : e.getEnd();
//
//                edges.add(new Edge<>(newStart, newEnd, e.getData()));
//            });
//        }
//
//        fixNodes();
    }

    private void simplifySimilarDummyNodes() {
        // Find all the nodes that only have dummy activities exiting from them...
        List<Node> nodesWithNoNonDummyExitActivities = nodes.stream()
                .filter(n -> edges.stream().noneMatch(e -> e.getStart().equals(n) && e.getData() != null))
                .collect(toList());

        // Reduce the list to only nodes that have at least two exits...
        nodesWithNoNonDummyExitActivities.removeIf(n -> edges.stream().filter(e -> e.getStart().equals(n)).count() < 2);

        Map<Node, Set<Long>> nodeTargets = new HashMap<>();

        nodesWithNoNonDummyExitActivities.forEach(n -> nodeTargets.put(n, edges.stream()
                .filter(e -> e.getStart().equals(n)).map(e -> e.getEnd().getId()).collect(toSet())
        ));

        while (nodesWithNoNonDummyExitActivities.size() > 1) {
            Node nodeToCompare = nodesWithNoNonDummyExitActivities.remove(0);

            for (Node otherNode : nodesWithNoNonDummyExitActivities) {
                if (nodeTargets.get(nodeToCompare).equals(nodeTargets.get(otherNode))) {
                    List<Edge<T>> otherNodeExits = edges.stream()
                            .filter(e -> e.getStart().equals(otherNode))
                            .collect(toList());

                    Edge<T> edgeToUpdate = otherNodeExits.remove(0);
                    edges.remove(edgeToUpdate);
                    edges.add(new Edge<>(edgeToUpdate.getStart(), nodeToCompare, edgeToUpdate.getData()));

                    edges.removeAll(otherNodeExits);

                    nodeTargets.get(otherNode).clear();
                    nodeTargets.get(otherNode).add(nodeToCompare.getId());
                }
            }

        }
    }

    private void collapseDummiesThatAreOnlyExitFromNodes() {
        for (Node node : nodes) {
            Set<Edge<T>> nodeEdges = edges.stream().filter(e -> e.getStart().equals(node)).collect(toSet());
            if (nodeEdges.size() == 1) {
                nodeEdges.forEach(e -> {
                    if (e.getData() == null) {
                        Set<Node> startConnections = getNodesConnectedTo(e.getStart());
                        Set<Node> endConnections = getNodesConnectedTo(e.getEnd());
                        if (Collections.disjoint(startConnections, endConnections)) {
                            collapseEdge(e);
                        }
                    }
                });
            }
        }
    }

    private Set<Node> getNodesConnectedTo(Node node) {
        return edges.stream()
                .filter(e -> e.getStart().equals(node) || e.getEnd().equals(node))
                .flatMap(e -> Stream.of(e.getStart(), e.getEnd()))
                .filter(n -> !n.equals(node))
                .collect(toSet());
    }

}
