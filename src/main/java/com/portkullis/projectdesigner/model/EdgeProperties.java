package com.portkullis.projectdesigner.model;

import java.util.Objects;

public class EdgeProperties {

    private final String label;
    private final int duration;

    public EdgeProperties(String label, int duration) {
        this.label = label;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "EdgeProperties{" +
                "label='" + label + '\'' +
                ", duration=" + duration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeProperties that = (EdgeProperties) o;
        return duration == that.duration &&
                Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, duration);
    }

    public String getLabel() {
        return label;
    }

    public int getDuration() {
        return duration;
    }

}
