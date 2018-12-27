package com.portkullis.projectdesigner;

import com.portkullis.projectdesigner.engine.impl.VisualizationEngineImpl;
import com.portkullis.projectdesigner.model.Activity;
import com.portkullis.projectdesigner.model.EdgeProperties;

public class UtilitySpike extends AbstractVisualizationSpike {

    private UtilitySpike() {
        super(new VisualizationEngineImpl<>(Activity::getId, Activity::getPredecessors, a -> new EdgeProperties(Long.toString(a.getId()), a.getDuration())));
    }

    public static void main(String[] args) {
        new UtilitySpike().run();
    }

    @Override
    protected void defineActivities() {
        addActivity(1, "Requirements", 15);
        addActivity(2, "Architecture", 20, 1);
        addActivity(3, "Project Planning", 20, 2);
        addActivity(4, "Test Plan", 30, 3);
        addActivity(5, "Test Harness", 33, 4);
        addActivity(6, "Logging", 15, 3);
        addActivity(7, "Security", 20, 3);
        addActivity(8, "Pub/Sub", 5, 3);
        addActivity(9, "Resource A", 20, 3);
        addActivity(10, "Resource B", 15, 3);
        addActivity(11, "Resource Access A", 10, 6, 9);
        addActivity(12, "Resource Access B", 5, 6, 10);
        addActivity(13, "Resource Access C", 16, 6);
        addActivity(14, "Engine A", 20, 12, 13);
        addActivity(15, "Engine B", 25, 12, 13);
        addActivity(16, "Engine C", 15, 6);
        addActivity(17, "Manager A", 20, 7, 8, 11, 14, 15);
        addActivity(18, "Manager B", 25, 7, 8, 15, 16);
        addActivity(19, "Client App 1", 25, 17, 18);
        addActivity(20, "Client App 2", 35, 17);
        addActivity(21, "System Test", 30, 5, 19, 20);
    }

}
