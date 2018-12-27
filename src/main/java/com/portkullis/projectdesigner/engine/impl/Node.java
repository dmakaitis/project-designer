package com.portkullis.projectdesigner.engine.impl;

import java.util.Objects;

/**
 * A node in a project arrow diagram.
 */
public class Node implements Comparable<Node> {

    private final long id;
    private String label;

    /**
     * Constructs the node.
     *
     * @param id the node ID.
     */
    public Node(long id) {
        this.id = id;
    }

    /**
     * Constructs the node.
     *
     * @param id    the node ID.
     * @param label the node label.
     */
    Node(long id, String label) {
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
    public int compareTo(Node o) {
        return Long.compare(id, o.id);
    }

    long getId() {
        return id;
    }

    String getLabel() {
        return label;
    }

    void setLabel(String label) {
        this.label = label;
    }

}
