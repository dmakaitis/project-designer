package com.portkullis.projectdesigner;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Arrays.asList;

public class UtilitySpike extends AbstractVisualizationSpike {

    public static final String DEVELOPER = "Developer";
    public static final String DBA = "DBA";

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
        addActivity(6, "Logging", 15, DEVELOPER, 3);
        addActivity(7, "Security", 20, DEVELOPER, 3);
        addActivity(8, "Pub/Sub", 5, DEVELOPER, 3);
        addActivity(9, "Resource A", 20, DBA, 3);
        addActivity(10, "Resource B", 15, DBA, 3);
        addActivity(11, "Resource Access A", 10, DEVELOPER, 6, 9);
        addActivity(12, "Resource Access B", 5, DEVELOPER, 6, 10);
        addActivity(13, "Resource Access C", 16, DEVELOPER, 6);
        addActivity(14, "Engine A", 20, DEVELOPER, 12, 13);
        addActivity(15, "Engine B", 25, DEVELOPER, 12, 13);
        addActivity(16, "Engine C", 15, DEVELOPER, 6);
        addActivity(17, "Manager A", 20, DEVELOPER, 7, 8, 11, 14, 15);
        addActivity(18, "Manager B", 25, DEVELOPER, 7, 8, 15, 16);
        addActivity(19, "Client App 1", 25, DEVELOPER, 17, 18);
        addActivity(20, "Client App 2", 35, DEVELOPER, 17);
        addActivity(21, "System Test", 30, 5, 19, 20);
    }

    @Override
    protected void defineResources(Set<String> resources, Map<String, SortedSet<String>> resourceTypes) {
        resources.add("Project Manager");
        resources.add("Product Manager");
        resources.add("Architect");

        resources.add("Developer 1");
//        resources.add("Developer 2");
//        resources.add("Developer 3");
//        resources.add("Developer 4");
//        resources.add("Developer 5");
//        resources.add("Developer 6");
//        resources.add("Developer 7");
//        resources.add("Developer 8");
//        resources.add("Developer 9");
//        resources.add("Developer 10");

        resources.add("DBA 1");
        resources.add("DBA 2");

        resources.add("QA 1");
        resources.add("QA 2");

        resources.add("Test Engineer");

        resources.add("Change Manager");

        resourceTypes.put(DBA, new TreeSet<>(asList(
                "DBA 1",
                "DBA 2"
        )));

        resourceTypes.put(DEVELOPER, new TreeSet<>(asList(
                "Developer 1"
//                "Developer 2",
//                "Developer 3",
//                "Developer 4",
//                "Developer 5",
//                "Developer 6",
//                "Developer 7",
//                "Developer 8",
//                "Developer 9",
//                "Developer 10"
        )));
    }

    @Override
    protected void assignResources() {
        assignResource(1, "Architect");
        assignResource(1, "Product Manager");

        assignResource(2, "Architect");

        assignResource(3, "Project Manager");
        assignResource(3, "Architect");

        assignResource(4, "Test Engineer");

        assignResource(5, "Test Engineer");

        assignResource(9, "DBA 1");
        assignResource(10, "DBA 2");

        assignResource(21, "QA 1");
        assignResource(21, "QA 2");


        assignResource(6, "Developer 1");
        assignResource(7, "Developer 1");
        assignResource(8, "Developer 1");
//        assignResource(13, "Developer 1");
//        assignResource(12, "Developer 1");
//        assignResource(16, "Developer 1");
//        assignResource(11, "Developer 1");
//        assignResource(15, "Developer 1");
//        assignResource(14, "Developer 1");
//        assignResource(17, "Developer 1");
//        assignResource(18, "Developer 1");
//        assignResource(20, "Developer 1");
//        assignResource(19, "Developer 1");
    }

}
