package com.portkullis.projectdesigner.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    public Set<Node> getNodes() {
        if (nodes == null) {
            nodes = new HashSet<>();
        }
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }

    public Set<Edge<T>> getEdges() {
        if (edges == null) {
            edges = new HashSet<>();
        }
        return edges;
    }

    public void setEdges(Set<Edge<T>> edges) {
        this.edges = edges;
    }

}
