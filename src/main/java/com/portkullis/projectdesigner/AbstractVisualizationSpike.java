package com.portkullis.projectdesigner;

import com.portkullis.projectdesigner.adapter.ProjectAssignmentDataAdapter;
import com.portkullis.projectdesigner.adapter.ProjectVisualizationDataAdapter;
import com.portkullis.projectdesigner.engine.AssignmentEngine;
import com.portkullis.projectdesigner.engine.CalculationEngine;
import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.engine.impl.AssignmentEngineImpl;
import com.portkullis.projectdesigner.engine.impl.CalculationEngineImpl;
import com.portkullis.projectdesigner.engine.impl.VisualizationEngineImpl;
import com.portkullis.projectdesigner.model.Activity;
import com.portkullis.projectdesigner.model.Project;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

public abstract class AbstractVisualizationSpike implements Runnable {

    private final VisualizationEngine visualizationEngine = new VisualizationEngineImpl();
    private final CalculationEngine calculationEngine = new CalculationEngineImpl();
    private final AssignmentEngine<Activity, String> assignmentEngine = new AssignmentEngineImpl<>();

    private final Project<Activity, String> project = new Project<>();
    private final Map<Integer, Activity> activityMap = new HashMap<>();

    private AssignmentEngine.ProjectData<Activity, String> projectData = new ProjectAssignmentDataAdapter(project);

    protected abstract void defineActivities();

    protected abstract void defineResources(Set<String> resources, Map<String, SortedSet<String>> resourceTypes);

    protected abstract void assignResources();

    @Override
    public void run() {
        System.out.println("Defining activities...");
        defineActivities();
        System.out.println("Defining resources...");
        defineResources(project.getResources(), project.getResourceTypes());
        System.out.println("Assigning resources...");
        assignResources();

        assignmentEngine.assignResources(projectData);

        project.getUtilityData().forEach(activity -> {
            System.out.println("Activity " + activity.getId() + " - " + activity.getDescription() + ": " + project.getActivityAssignments().get(activity));
//            System.out.println("          Start: " + calculationEngine.getEarliestStartTime(activity) + "/" + calculationEngine.getLatestStartTime(activity));
//            System.out.println("            End: " + calculationEngine.getEarliestEndTime(activity) + "/" + calculationEngine.getLatestEndTime(activity));
//            System.out.println("    Total Float: " + calculationEngine.getTotalFloat(activity));
        });
//        project.getResourceAssignments().forEach((resource, activities) -> {
//            System.out.print(resource + " =>");
//            activities.forEach(activity -> System.out.print(" " + activity.getId()));
//            System.out.println();
//        });

        Date timerStart = new Date();
        try {
            visualizationEngine.visualizeProject(new ProjectVisualizationDataAdapter(project));
//            visualizationEngine.visualizeGraph(new ProjectVisualizationDataAdapter<>(project).getActivityGraph());
        } finally {
            Date timerStop = new Date();
            System.out.println("Graph calculated in " + (timerStop.getTime() - timerStart.getTime()) + "ms");
        }
    }

    void addActivity(int activityId, String description, int duration, Integer... prerequisites) {
        addActivity(activityId, description, duration, null, prerequisites);
    }

    void addActivity(int activityId, String description, int duration, String type, Integer... prerequisites) {
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

        if (type != null) {
            project.getActivityTypes().put(activity, type);
        }
    }

    void assignResource(int activityId, String resource) {
        projectData.assignActivityToResource(activityMap.get(activityId), resource);
    }

    static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

}
