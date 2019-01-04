package com.portkullis.projectdesigner.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Activity {

    private final long id;
    private String description;
    private int duration;
    private Set<Activity> predecessors;

    public Activity(long id, String description, int duration, Set<Activity> predecessors) {
        this.id = id;
        this.description = description;
        this.duration = duration;
        this.predecessors = predecessors;
    }

    public Activity(long id, String description, int duration) {
        this(id, description, duration, new HashSet<>());
    }

    public Activity(long id, String description, int duration, Activity... activities) {
        this(id, description, duration, new HashSet<>(Arrays.asList(activities)));
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", predecessors=" + predecessors +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return id == activity.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Set<Activity> getPrerequisites() {
        return predecessors;
    }

    public void setPredecessors(Set<Activity> predecessors) {
        this.predecessors = predecessors;
    }
}
