package com.portkullis.projectdesigner;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

public class AmuiSpike extends AbstractVisualizationSpike {

    public static void main(String[] args) {
        new AmuiSpike().run();
    }

    @Override
    protected void defineActivities() {
        addActivity(1, "Requirements", 15);
        addActivity(2, "DetailedRequirements", 20, 1);
        addActivity(3, "Architecture", 20, 1);
        addActivity(4, "Architecture PoC", 30, 3);
        addActivity(5, "Project Design", 33, 4);
        addActivity(6, "Pub/Sub", 15, 2, 5);
        addActivity(7, "Logging", 20, 2, 5);
        addActivity(8, "Log Book", 5, 2, 5);
        addActivity(9, "Security", 20, 2, 5);
        addActivity(10, "Config", 15, 2, 5);
        addActivity(11, "Metrics", 10, 2, 5);
        addActivity(12, "Feature Toggles", 5, 2, 5);
        addActivity(13, "Caching", 16, 2, 5);
        addActivity(14, "API Gateway", 20, 2, 5);
        addActivity(15, "Notification Resource", 25, 7, 10, 11, 12);

        addActivity(47, "Milestone 0 - Core Utilities", 30, 6, 7, 9, 10, 11, 12, 13, 14);

        addActivity(16, "Account Repository", 15, 47);
        addActivity(17, "SAML Protocol Metadata Repository", 20, 47);
        addActivity(18, "Entitlement Repository", 25, 47);
        addActivity(19, "SAML Protocol Engine", 25, 17);
        addActivity(20, "Validation Engine", 35, 47);

        addActivity(46, "Activity Repository", 30, 47);

        addActivity(21, "Identity Workflow Manager", 30, 15, 16, 19, 20, 46);
        addActivity(22, "Account Manager", 30, 15, 16, 18, 20, 46);
        addActivity(23, "Service Provider Manager", 30, 17, 19, 20);

        addActivity(42, "UX Design", 30, 3);

        addActivity(24, "Identity Provider", 30, 42, 21);
        addActivity(25, "User Administrator Client", 30, 42, 22);
        addActivity(26, "Service Provider Admin UI", 30, 42, 23);
        addActivity(27, "User Profile Client", 30, 42, 22);
        addActivity(28, "SAML Client", 30, 24);
        addActivity(29, "Manage Party Subscriber", 30, 22);
        addActivity(30, "Legacy Client Adapter", 30, 21, 22, 23);
        addActivity(31, "Test Plan - Identity Provider", 30, 2);
        addActivity(32, "Test Plan - User Admin Client", 30, 2);
        addActivity(33, "Test Plan - Service Provider Admin UI", 30, 2);
        addActivity(34, "Test Plan - User Profile Client", 30, 2);
        addActivity(35, "Test Plan - SAML Client", 30, 2, 31);
        addActivity(36, "Test Automation - Identity Provider", 30, 24, 31);
        addActivity(37, "Test Automation - User Admin Client", 30, 25, 32);
        addActivity(38, "Test Automation - Service Provider Admin UI", 30, 26, 33);
        addActivity(39, "Test Automation - User Profile Client", 30, 27, 34);
        addActivity(40, "Test Automation - SAML Client", 30, 28, 35);
        addActivity(41, "System Testing", 30, 8, 29, 30, 31, 32, 33, 34, 35);
        addActivity(43, "System Testing - Automated Tests", 30, 36, 37, 38, 39, 40);
        addActivity(44, "Performance Testing", 30, 41);

        addActivity(48, "System Testing - Milestone 1", 30, 24, 25, 31, 32);
        addActivity(50, "Performance Testing - Milestone 1", 30, 48);
        addActivity(52, "Automated Testing - Milestone 1", 30, 36, 37);
        addActivity(51, "Deployment - Milestone 1", 30, 48, 50, 52);
        addActivity(49, "Milestone 1 - Identity Provider and User Admin Client", 30, 47, 51);

        addActivity(45, "Deployment", 30, 43, 44, 51, 49);
    }

    @Override
    protected void defineResources(Set<String> resources, Map<String, SortedSet<String>> resourceTypes) {

    }

    @Override
    protected void assignResources() {

    }

}
