package com.portkullis.projectdesigner;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

public class UpdatedAmuiNoMilestonesSpike extends AbstractVisualizationSpike {

    private static final String DEVELOPER = "Developer";
    private static final String TEST_ENGINEER = "Test Engineer";

    public static void main(String[] args) {
        new UpdatedAmuiNoMilestonesSpike().run();
    }

    @Override
    protected void defineActivities() {
        addActivity(1, "Architecture PoC", 10, DEVELOPER);

        addActivity(2, "Security", 10, DEVELOPER, 1);

        addActivity(3, "Account Repository", 20, DEVELOPER, 1);
        addActivity(4, "SAML Protocol Metadata Repository", 10, DEVELOPER, 1);
        addActivity(5, "Activity Repository", 5, DEVELOPER, 1);
        addActivity(6, "Entitlement Repository", 35, DEVELOPER, 1);
        addActivity(7, "Notifications RA", 10, DEVELOPER, 1);

        addActivity(9, "SAML Protocol Engine", 40, DEVELOPER, 4);
        addActivity(10, "Validation Engine", 15, DEVELOPER, 1);

        addActivity(11, "Identity Workflow Manager", 70, DEVELOPER, 3, 5, 6, 7, 9, 10);

//        addActivity(12, "Test Plan - Identity Provider", 10, DEVELOPER);
//        addActivity(13, "System Testing - Identity Provider", 10, DEVELOPER, 11, 12);

        addActivity(14, "Account Manager", 20, DEVELOPER, 2, 3, 5, 6, 7, 10);
        addActivity(15, "Service Provider Manager", 20, DEVELOPER, 2, 9, 10);

        addActivity(17, "UX Design - Identity Provider", 10, DEVELOPER, 1);
        addActivity(18, "UX Design - User Administrator Client", 10, DEVELOPER, 1);
        addActivity(19, "UX Design - Service Provider Admin UI", 10, DEVELOPER, 1);
        addActivity(20, "UX Design - User Profile Client", 10, DEVELOPER, 1);

        addActivity(21, "Identity Provider", 30, DEVELOPER, 11, 17);
        addActivity(22, "User Administrator Client", 25, DEVELOPER, 14, 18);
        addActivity(23, "Service Provider Admin UI", 25, DEVELOPER, 15, 19);
        addActivity(24, "User Profile Client", 25, DEVELOPER, 14, 20);
        addActivity(25, "SAML Client", 10, DEVELOPER, 21);
        addActivity(26, "Manage Party Subscriber", 15, DEVELOPER, 14);
        addActivity(27, "Legacy Client Adapter", 5, DEVELOPER, 11, 14, 15);

        addActivity(28, "Test Plan - Identity Provider", 5, DEVELOPER, 1);
        addActivity(29, "Test Plan - User Admin Client", 10, DEVELOPER, 1);
        addActivity(30, "Test Plan - Service Provider Admin UI", 5, DEVELOPER, 1);
        addActivity(31, "Test Plan - User Profile Client", 10, DEVELOPER, 1);
        addActivity(32, "System Testing - Identity Provider", 5, DEVELOPER, 21, 28);
        addActivity(33, "System Testing - User Admin Client", 10, DEVELOPER, 22, 29);
        addActivity(34, "System Testing - Service Provider Admin UI", 5, DEVELOPER, 23, 30);
        addActivity(35, "System Testing - User Profile Client", 10, DEVELOPER, 24, 31);

//        addActivity(36, "Deployment", 10, DEVELOPER,13, 32, 33, 34, 35);
        addActivity(36, "Deployment", 10, DEVELOPER, 32, 33, 34, 35);

    }

    @Override
    protected void defineResources(Set<String> resources, Map<String, SortedSet<String>> resourceTypes) {
        resources.add("Project Manager");
        resources.add("Product Manager");
        resources.add("Architect");

        resources.add("UX Designer");

        SortedSet<String> developers = makeResources("Developer", 4);
        resources.addAll(developers);
        resourceTypes.put(DEVELOPER, developers);

        SortedSet<String> testEngineers = makeResources("Test Engineer", 1);
        resources.addAll(testEngineers);
        resourceTypes.put(TEST_ENGINEER, testEngineers);
    }

    @Override
    protected void assignResources() {
//        assignResource(1, "Architect");
//        assignResource(1, "Product Manager");

//        assignResource(2, "Architect");
//        assignResource(2, "Product Manager");

//        assignResource(3, "Architect");

//        assignResource(5, "Architect");
//        assignResource(5, "Project Manager");

//        assignResource(42, "UX Designer");
    }

}
