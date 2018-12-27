package com.portkullis.projectdesigner;

import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.model.Activity;
import com.portkullis.projectdesigner.model.Project;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

public abstract class AbstractVisualizationSpike implements Runnable {

    private final VisualizationEngine<Activity> visualizationEngine;

    private final Project<Activity> project = new Project<>();
    private final Map<Integer, Activity> activityMap = new HashMap<>();

    AbstractVisualizationSpike(VisualizationEngine<Activity> visualizationEngine) {
        this.visualizationEngine = visualizationEngine;
    }

    protected abstract void defineActivities();

    @Override
    public void run() {
        defineActivities();

        Date timerStart = new Date();
        try {
            visualizationEngine.visualizeProject(project);
        } finally {
            Date timerStop = new Date();
            System.out.println("Graph calculated in " + (timerStop.getTime() - timerStart.getTime()) + "ms");
        }
    }

    void addActivity(int activityId, String description, int duration, Integer... prerequisites) {
        List<Integer> prerequisitesList = asList(prerequisites);
        Set<Activity> prereqs = activityMap.entrySet().stream()
                .filter(e -> prerequisitesList.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .collect(toSet());

        if (prereqs.size() != prerequisites.length) {
            throw new RuntimeException("Could not find all prerequisites for activity " + activityId + ": " + prerequisitesList);
        }

        Activity activity = new Activity(activityId, description, duration, prereqs);

        activityMap.put(activityId, activity);
        project.getUtilityData().add(activity);
    }

}
