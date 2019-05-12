package com.portkullis.projectdesigner;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Arrays.asList;

public class ExampleSpike extends AbstractVisualizationSpike {

    private static final String DEVELOPER = "Developer";
    private static final String TEST_ENGINEER = "Test Engineer";

    public static void main(String[] args) {
        new ExampleSpike().run();
    }

    @Override
    protected void defineActivities() {
        addActivity(1, "Architecture", 5);
        addActivity(2, "Project Design", 20, 1);
        addActivity(3, "Test Plan", 15, 2);
        addActivity(4, "UI Design", 20, 2);
        addActivity(5, "Logging", 10, DEVELOPER, 2);
        addActivity(6, "Security", 15, DEVELOPER, 2);
        addActivity(7, "Config", 10, DEVELOPER, 2);
        addActivity(8, "Metrics", 15, DEVELOPER, 2);
        addActivity(9, "Caching", 5, DEVELOPER, 2);

        addActivity(10, "Infrastructure Complete", 0, 5, 6, 7, 8, 9);

        addActivity(11, "Resource A", 10, DEVELOPER, 2);
        addActivity(12, "Resource B", 10, DEVELOPER, 2);

        addActivity(20, "Resources Complete", 0, 11, 12);

        addActivity(13, "Resource Access A", 15, DEVELOPER, 10, 20);
        addActivity(14, "Resource Access B", 15, DEVELOPER, 10, 20);

        addActivity(21, "Resource Access Complete", 0, 13, 14);

        addActivity(15, "Engine", 10, DEVELOPER, 21);
        addActivity(16, "Manager", 10, DEVELOPER, 21, 15);

        addActivity(17, "Client", 15, DEVELOPER, 4, 16);
        addActivity(18, "System Testing", 10, 3, 17);
        addActivity(19, "Deployment", 5, DEVELOPER, 18);
    }

    @Override
    protected void defineResources(Set<String> resources, Map<String, SortedSet<String>> resourceTypes) {
        resources.add("Project Manager");
        resources.add("Product Manager");
        resources.add("Architect");

        resources.add("UX Designer");

        resources.add("Developer 1");
        resources.add("Developer 2");
        resources.add("Developer 3");
        resources.add("Developer 4");
        resources.add("Developer 5");
        resources.add("Developer 6");
        resources.add("Developer 7");
        resources.add("Developer 8");
        resources.add("Developer 9");

        resourceTypes.put(DEVELOPER, new TreeSet<>(asList(
                "Developer 1",
                "Developer 2",
                "Developer 3",
                "Developer 4",
                "Developer 5",
                "Developer 6",
                "Developer 7",
                "Developer 8",
                "Developer 9"
        )));

        resources.add("Test Engineer 1");

        resourceTypes.put(TEST_ENGINEER, new TreeSet<>(asList(
                "Test Engineer 1"
        )));
    }

    @Override
    protected void assignResources() {
        assignResource(1, "Architect");
        assignResource(1, "Product Manager");

        assignResource(2, "Architect");
        assignResource(2, "Product Manager");

        assignResource(3, "Architect");

        assignResource(18, "Architect");

        assignResource(4, "UX Designer");
    }

}
