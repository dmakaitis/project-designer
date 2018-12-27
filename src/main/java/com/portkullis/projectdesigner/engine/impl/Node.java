package com.portkullis.projectdesigner.engine.impl;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Node implements Comparable<Node> {

    private final long id;
    private String label;

    public Node(long id) {
        this.id = id;
    }

    public Node(long id, String label) {
        this(id);
        this.label = label;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", label='" + label + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(@NotNull Node o) {
        return Long.compare(id, o.id);
    }

    public long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
