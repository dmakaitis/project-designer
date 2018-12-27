package com.portkullis.projectdesigner.engine.impl;

import java.util.Objects;

public class Edge<T> {

    private final Node start;
    private final Node end;
    private T data;

    public Edge(Node start, Node end) {
        this(start, end, null);
    }

    public Edge(Node start, Node end, T data) {
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

    public Node getStart() {
        return start;
    }

    public Node getEnd() {
        return end;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
