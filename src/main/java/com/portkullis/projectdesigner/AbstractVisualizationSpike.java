package com.portkullis.projectdesigner;

import com.portkullis.projectdesigner.adapter.ProjectDataAdapter;
import com.portkullis.projectdesigner.engine.AssignmentEngine;
import com.portkullis.projectdesigner.engine.CalculationEngine;
import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.engine.impl.AssignmentEngineImpl;
import com.portkullis.projectdesigner.engine.impl.CalculationEngineImpl;
import com.portkullis.projectdesigner.engine.impl.VisualizationEngineImpl;
import com.portkullis.projectdesigner.model.Activity;
import com.portkullis.projectdesigner.model.EdgeProperties;
import com.portkullis.projectdesigner.model.Project;

import java.util.*;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

public abstract class AbstractVisualizationSpike implements Runnable {

    private final VisualizationEngine<Activity> visualizationEngine;
    private final CalculationEngine<Activity> calculationEngine;
    private final AssignmentEngine<Activity, String> assignmentEngine;

    private final Project<Activity, String> project = new Project<>();
    private final Map<Integer, Activity> activityMap = new HashMap<>();

    private AssignmentEngine.ProjectData<Activity, String> projectData = new ProjectDataAdapter<>(project);

    AbstractVisualizationSpike() {
        Function<Activity, Collection<Activity>> prerequisiteMapper = activity -> {
//            Set<Activity> prerequisites = project.getResourceAssignments().values().stream()
//                    .filter(a -> a.contains(activity))
//                    .flatMap(a -> a.headSet(activity).stream())
//                    .collect(toSet());
//            prerequisites.addAll(activity.getPrerequisites());
//
////            if(isEmpty(project.getActivityAssignments().get(activity))) {
////                System.out.println("Activity " + activity.getId() + " is unassigned...");
////            }
//
//            return prerequisites;
            return activity.getPrerequisites();
        };
        Function<Activity, Collection<Activity>> directSuccessorMapper = activity -> project.getUtilityData().stream()
                .filter(a -> prerequisiteMapper.apply(a).contains(activity))
                .collect(toSet());

        this.calculationEngine = new CalculationEngineImpl<>(prerequisiteMapper, directSuccessorMapper, Activity::getDuration);
//        Comparator<Activity> activitySorter = Comparator.comparing(calculationEngine::getEarliestEndTime).thenComparing(calculationEngine::getTotalFloat);

        Function<Activity, EdgeProperties> edgePropertyMapper = activity -> new EdgeProperties(Long.toString(activity.getId()), activity.getDuration());
        this.visualizationEngine = new VisualizationEngineImpl<>(Activity::getId, prerequisiteMapper, edgePropertyMapper);

        this.assignmentEngine = new AssignmentEngineImpl<>();
    }

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

//        assignmentEngine.assignResources(project);

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
            visualizationEngine.visualizeProject(project);
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
