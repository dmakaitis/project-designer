package com.portkullis.projectdesigner.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A software development project.
 *
 * @author darius
 */
public class Project<A> {

    private List<A> utilityData;

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

}
