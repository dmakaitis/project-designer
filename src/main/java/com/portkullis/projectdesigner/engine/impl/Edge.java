package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.exception.ProjectDesignerRuntimeException;

import java.util.Objects;
import java.util.Optional;

/**
 * An edge in a project arrow diagram.
 *
 * @param <T> the activity type.
 */
public class Edge<T> {

    private final Node start;
    private final Node end;
    private final T data;

    /**
     * Constructs the edge. Edges without activity data are considered to be dummy nodes.
     *
     * @param start the starting node.
     * @param end   the ending node.
     */
    public Edge(Node start, Node end) {
        this(start, end, null);
    }

    /**
     * Constructs the edge with the given activity data.
     *
     * @param start the starting node.
     * @param end   the ending node.
     * @param data  the activity data.
     */
    public Edge(Node start, Node end, T data) {
        if (start == null) {
            throw new ProjectDesignerRuntimeException("Edges must always have a starting node");
        }
        if (end == null) {
            throw new ProjectDesignerRuntimeException("Edges must always have an ending node");
        }

        this.start = start;
        this.end = end;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "start=" + start +
                ", end=" + end +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge<?> edge = (Edge<?>) o;
        return Objects.equals(start, edge.start) &&
                Objects.equals(end, edge.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    /**
     * Gets the starting node.
     *
     * @return the starting node.
     */
    public Node getStart() {
        return start;
    }

    /**
     * Gets the ending node.
     *
     * @return the ending node.
     */
    public Node getEnd() {
        return end;
    }

    /**
     * Gets the activity data, if any.
     *
     * @return the activity data.
     */
    Optional<T> getData() {
        return Optional.ofNullable(data);
    }

}
