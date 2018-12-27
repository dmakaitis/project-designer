package com.portkullis.projectdesigner;

import com.portkullis.projectdesigner.engine.VisualizationEngine;
import com.portkullis.projectdesigner.engine.impl.VisualizationEngineImpl;
import com.portkullis.projectdesigner.model.Activity;
import com.portkullis.projectdesigner.model.EdgeProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UtilitySpike {

    private static final VisualizationEngine<Activity> visualizationEngine = new VisualizationEngineImpl<>(Activity::getId, Activity::getPredecessors, a -> new EdgeProperties(Long.toString(a.getId())));

    public static void main(String[] args) {
        List<Activity> utilityData = new ArrayList<>();

        Activity activity1 = addActivity(utilityData, new Activity(1, "Requirements", 15));
        Activity activity2 = addActivity(utilityData, new Activity(2, "Architecture", 20, activity1));
        Activity activity3 = addActivity(utilityData, new Activity(3, "Project Planning", 20, activity2));
        Activity activity4 = addActivity(utilityData, new Activity(4, "Test Plan", 30, activity3));
        Activity activity5 = addActivity(utilityData, new Activity(5, "Test Harness", 33, activity4));
        Activity activity6 = addActivity(utilityData, new Activity(6, "Logging", 15, activity3));
        Activity activity7 = addActivity(utilityData, new Activity(7, "Security", 20, activity3));
        Activity activity8 = addActivity(utilityData, new Activity(8, "Pub/Sub", 5, activity3));
        Activity activity9 = addActivity(utilityData, new Activity(9, "Resource A", 20, activity3));
        Activity activity10 = addActivity(utilityData, new Activity(10, "Resource B", 15, activity3));
        Activity activity11 = addActivity(utilityData, new Activity(11, "Resource Access A", 10, activity6, activity9));
        Activity activity12 = addActivity(utilityData, new Activity(12, "Resource Access B", 5, activity6, activity10));
        Activity activity13 = addActivity(utilityData, new Activity(13, "Resource Access C", 16, activity6));
        Activity activity14 = addActivity(utilityData, new Activity(14, "Engine A", 20, activity12, activity13));
        Activity activity15 = addActivity(utilityData, new Activity(15, "Engine B", 25, activity12, activity13));
        Activity activity16 = addActivity(utilityData, new Activity(16, "Engine C", 15, activity6));
        Activity activity17 = addActivity(utilityData, new Activity(17, "Manager A", 20, activity7, activity8, activity11, activity14, activity15));
        Activity activity18 = addActivity(utilityData, new Activity(18, "Manager B", 25, activity7, activity8, activity15, activity16));
        Activity activity19 = addActivity(utilityData, new Activity(19, "Client App 1", 25, activity17, activity18));
        Activity activity20 = addActivity(utilityData, new Activity(20, "Client App 2", 35, activity17));
        Activity activity21 = addActivity(utilityData, new Activity(21, "System Test", 30, activity5, activity19, activity20));

        Date timerStart = new Date();
        try {
            visualizationEngine.visualizeUtilityData(utilityData);
        } finally {
            Date timerStop = new Date();
            System.out.println("Graph calculated in " + (timerStop.getTime() - timerStart.getTime()) + "ms");
        }
    }

    private static Activity addActivity(List<Activity> utilityData, Activity activity) {
        utilityData.add(activity);
        return activity;
    }

}
