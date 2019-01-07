package com.portkullis.projectdesigner.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A software development project.
 *
 * @author darius
 */
public class Project<A, R> {

    private List<A> utilityData;
    private Map<A, String> activityTypes;

    private Map<String, Plan<A, R>> plans;
    private String activePlan;

    /**
     * Returns the utility data for the project.
     *
     * @return the project utility data.
     */
    public List<A> getUtilityData() {
        if (utilityData == null) {
            utilityData = new ArrayList<>();
        }
        return utilityData;
    }

    /**
     * Returns the mapping of activities to resource types.
     *
     * @return the activity resource type map.
     */
    public Map<A, String> getActivityTypes() {
        if (activityTypes == null) {
            activityTypes = new HashMap<>();
        }
        return activityTypes;
    }

    /**
     * Returns the collection of plans for the project.
     *
     * @return the collection of plans for the project.
     */
    public Map<String, Plan<A, R>> getPlans() {
        if (plans == null) {
            plans = new HashMap<>();
        }
        return plans;
    }

    /**
     * Returns the active plan for the project.
     *
     * @return the active plan for the project.
     */
    public String getActivePlan() {
        return activePlan;
    }

    /**
     * Sets the active plan for the project.
     *
     * @param activePlan the active plan for the project.
     */
    public void setActivePlan(String activePlan) {
        this.activePlan = activePlan;
    }

}
