package com.portkullis.projectdesigner;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Arrays.asList;

public class AmuiSpike extends AbstractVisualizationSpike {

    public static final String DEVELOPER = "Developer";
    public static final String TEST_ENGINEER = "Test Engineer";

    public static void main(String[] args) {
        new AmuiSpike().run();
    }

    @Override
    protected void defineActivities() {
        addActivity(1, "Requirements", 15);
        addActivity(2, "DetailedRequirements", 20, 1);
        addActivity(3, "Architecture", 20, 1);
        addActivity(4, "Architecture PoC", 30, DEVELOPER, 3);
        addActivity(5, "Project Design", 33, 4);
        addActivity(6, "Pub/Sub", 15, DEVELOPER, 2, 5);
        addActivity(7, "Logging", 20, DEVELOPER, 2, 5);
        addActivity(8, "Log Book", 5, DEVELOPER, 2, 5);
        addActivity(9, "Security", 20, DEVELOPER, 2, 5);
        addActivity(10, "Config", 15, DEVELOPER, 2, 5);
        addActivity(11, "Metrics", 10, DEVELOPER, 2, 5);
        addActivity(12, "Feature Toggles", 5, DEVELOPER, 2, 5);
        addActivity(13, "Caching", 16, DEVELOPER, 2, 5);
        addActivity(14, "API Gateway", 20, DEVELOPER, 2, 5);
        addActivity(15, "Notification Resource", 25, DEVELOPER, 7, 10, 11, 12);

        addActivity(47, "Milestone 0 - Core Utilities", 30, 6, 7, 9, 10, 11, 12, 13, 14);

        addActivity(16, "Account Repository", 15, DEVELOPER, 47);
        addActivity(17, "SAML Protocol Metadata Repository", 20, DEVELOPER, 47);
        addActivity(18, "Entitlement Repository", 25, DEVELOPER, 47);
        addActivity(19, "SAML Protocol Engine", 25, DEVELOPER, 17);
        addActivity(20, "Validation Engine", 35, DEVELOPER, 47);

        addActivity(46, "Activity Repository", 30, DEVELOPER, 47);

        addActivity(21, "Identity Workflow Manager", 30, DEVELOPER, 15, 16, 19, 20, 46);
        addActivity(22, "Account Manager", 30, DEVELOPER, 15, 16, 18, 20, 46);
        addActivity(23, "Service Provider Manager", 30, DEVELOPER, 17, 19, 20);

        addActivity(42, "UX Design", 30, 3);

        addActivity(24, "Identity Provider", 30, DEVELOPER, 42, 21);
        addActivity(25, "User Administrator Client", 30, DEVELOPER, 42, 22);
        addActivity(26, "Service Provider Admin UI", 30, DEVELOPER, 42, 23);
        addActivity(27, "User Profile Client", 30, DEVELOPER, 42, 22);
        addActivity(28, "SAML Client", 30, DEVELOPER, 24);
        addActivity(29, "Manage Party Subscriber", 30, DEVELOPER, 22);
        addActivity(30, "Legacy Client Adapter", 30, DEVELOPER, 21, 22, 23);
        addActivity(31, "Test Plan - Identity Provider", 30, TEST_ENGINEER, 2);
        addActivity(32, "Test Plan - User Admin Client", 30, TEST_ENGINEER, 2);
        addActivity(33, "Test Plan - Service Provider Admin UI", 30, TEST_ENGINEER, 2);
        addActivity(34, "Test Plan - User Profile Client", 30, TEST_ENGINEER, 2);
        addActivity(35, "Test Plan - SAML Client", 30, TEST_ENGINEER, 2, 31);
        addActivity(36, "Test Automation - Identity Provider", 30, DEVELOPER, 24, 31);
        addActivity(37, "Test Automation - User Admin Client", 30, DEVELOPER, 25, 32);
        addActivity(38, "Test Automation - Service Provider Admin UI", 30, DEVELOPER, 26, 33);
        addActivity(39, "Test Automation - User Profile Client", 30, DEVELOPER, 27, 34);
        addActivity(40, "Test Automation - SAML Client", 30, DEVELOPER, 28, 35);
        addActivity(41, "System Testing", 30, TEST_ENGINEER, 8, 29, 30, 31, 32, 33, 34, 35);
        addActivity(43, "System Testing - Automated Tests", 30, DEVELOPER, 36, 37, 38, 39, 40);
        addActivity(44, "Performance Testing", 30, DEVELOPER, 41);

        addActivity(48, "System Testing - Milestone 1", 30, TEST_ENGINEER, 24, 25, 31, 32);
        addActivity(50, "Performance Testing - Milestone 1", 30, DEVELOPER, 48);
        addActivity(52, "Automated Testing - Milestone 1", 30, DEVELOPER, 36, 37);
        addActivity(51, "Deployment - Milestone 1", 30, DEVELOPER, 48, 50, 52);
        addActivity(49, "Milestone 1 - Identity Provider and User Admin Client", 30, 47, 51);

        addActivity(45, "Deployment", 30, DEVELOPER, 43, 44, 51, 49);
    }

    @Override
    protected void defineResources(Set<String> resources, Map<String, SortedSet<String>> resourceTypes) {
        resources.add("Project Manager");
        resources.add("Product Manager");
        resources.add("Architect");

        resources.add("UX Designer");

        resources.add("Developer 1");
        resources.add("Developer 2");
//        resources.add("Developer 3");
//        resources.add("Developer 4");
//        resources.add("Developer 5");
//        resources.add("Developer 6");
//        resources.add("Developer 7");
//        resources.add("Developer 8");
//        resources.add("Developer 9");

        resourceTypes.put(DEVELOPER, new TreeSet<>(asList(
                "Developer 1",
                "Developer 2"
//                "Developer 3",
//                "Developer 4",
//                "Developer 5",
//                "Developer 6",
//                "Developer 7",
//                "Developer 8",
//                "Developer 9"
        )));

        resources.add("Test Engineer 1");
//        resources.add("Test Engineer 2");
//        resources.add("Test Engineer 3");
//        resources.add("Test Engineer 4");
//        resources.add("Test Engineer 5");
//        resources.add("Test Engineer 6");
//        resources.add("Test Engineer 7");
//        resources.add("Test Engineer 8");
//        resources.add("Test Engineer 9");

        resourceTypes.put(TEST_ENGINEER, new TreeSet<>(asList(
                "Test Engineer 1"
//                "Test Engineer 2",
//                "Test Engineer 3",
//                "Test Engineer 4",
//                "Test Engineer 5",
//                "Test Engineer 6",
//                "Test Engineer 7",
//                "Test Engineer 8",
//                "Test Engineer 9"
        )));
    }

    @Override
    protected void assignResources() {
        assignResource(1, "Architect");
        assignResource(1, "Product Manager");

        assignResource(2, "Architect");
        assignResource(2, "Product Manager");

        assignResource(3, "Architect");

        assignResource(5, "Architect");
        assignResource(5, "Project Manager");

        assignResource(42, "UX Designer");
    }

}
