package com.portkullis.projectdesigner.model;

import java.util.Objects;

public class Edge<T> {

    private final long id;
    private Node start;
    private Node end;
    private T data;

    public Edge(long id) {
        this(id, null, null, null);
    }

    public Edge(long id, Node start, Node end) {
        this(id, start, end, null);
    }

    public Edge(long id, Node start, Node end, T data) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Edge{");
        sb.append("id=").append(id);
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge<?> edge = (Edge<?>) o;
        return id == edge.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public long getId() {
        return id;
    }

    public Node getStart() {
        return start;
    }

    public void setStart(Node start) {
        this.start = start;
    }

    public Node getEnd() {
        return end;
    }

    public void setEnd(Node end) {
        this.end = end;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
