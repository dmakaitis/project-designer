package com.portkullis.projectdesigner;

import com.portkullis.projectdesigner.adapter.ProjectAssignmentDataAdapter;
import com.portkullis.projectdesigner.adapter.ProjectVisualizationDataAdapter;
import com.portkullis.projectdesigner.engine.AssignmentEngine;
import com.portkullis.projectdesigner.engine.CalculationEngine;
import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.engine.impl.AssignmentEngineImpl;
import com.portkullis.projectdesigner.engine.impl.CalculationEngineImpl;
import com.portkullis.projectdesigner.engine.impl.VisualizationEngineImpl;
import com.portkullis.projectdesigner.exception.ProjectDesignerRuntimeException;
import com.portkullis.projectdesigner.model.Activity;
import com.portkullis.projectdesigner.model.Plan;
import com.portkullis.projectdesigner.model.Project;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

public abstract class AbstractVisualizationSpike implements Runnable {

    private final VisualizationEngine visualizationEngine = new VisualizationEngineImpl();
    private final CalculationEngine calculationEngine = new CalculationEngineImpl();
    private final AssignmentEngine<Activity, String> assignmentEngine = new AssignmentEngineImpl<>();

    private final Project<Activity, String> project = new Project<>();
    private final Plan<Activity, String> plan = new Plan<>();
    private final Map<Integer, Activity> activityMap = new HashMap<>();

    private AssignmentEngine.ProjectData<Activity, String> projectData = new ProjectAssignmentDataAdapter(project);

    protected abstract void defineActivities();

    protected abstract void defineResources(Set<String> resources, Map<String, SortedSet<String>> resourceTypes);

    protected abstract void assignResources();

    @Override
    public void run() {
        project.setActivePlan("Test");
        project.getPlans().put("Test", plan);

        System.out.println("Defining activities...");
        defineActivities();
        System.out.println("Defining resources...");
        defineResources(plan.getResources(), plan.getResourceTypes());
        System.out.println("Assigning resources...");
        assignResources();

        assignmentEngine.assignResources(projectData);

        project.getUtilityData().forEach(activity -> System.out.println("Activity "
                + activity.getId() + " - "
                + activity.getDescription() + ": "
                + plan.getActivityAssignments().get(activity) + " - "
                + projectData.getEarliestFinish(activity))
        );

        System.out.println("Total duration: " + project.getUtilityData().stream()
                .mapToInt(projectData::getEarliestFinish)
                .max().orElse(0)
        );

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
        if (activityMap.containsKey(activityId)) {
            throw new ProjectDesignerRuntimeException("Can not add activity with duplicate activity ID: " + activityId);
        }

        List<Integer> prerequisitesList = asList(prerequisites);
        Set<Activity> prereqs = activityMap.entrySet().stream()
                .filter(e -> prerequisitesList.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .collect(toSet());

        if (prereqs.size() != prerequisites.length) {
            throw new ProjectDesignerRuntimeException("Could not find all prerequisites for activity " + activityId + ": " + prerequisitesList);
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

    SortedSet<String> makeResources(String type, int count) {
        SortedSet<String> resources = new TreeSet<>();
        for (int i = 0; i < count; i++) {
            resources.add(type + " " + (i + 1));
        }
        return resources;
    }

}
