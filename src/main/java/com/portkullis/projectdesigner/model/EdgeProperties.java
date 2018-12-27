package com.portkullis.projectdesigner.model;

public class EdgeProperties {

    private final String label;
    private final int duration;

    public EdgeProperties(String label, int duration) {
        this.label = label;
        this.duration = duration;
    }

    public String getLabel() {
        return label;
    }

    public int getDuration() {
        return duration;
    }

}
