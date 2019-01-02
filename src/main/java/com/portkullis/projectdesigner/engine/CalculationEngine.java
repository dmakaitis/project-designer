package com.portkullis.projectdesigner.engine;

import java.util.Collection;

/**
 * Methods that perform calculations on projects.
 *
 * @param <A> activity type.
 * @author darius
 */
public interface CalculationEngine<A> {

    /**
     * Returns the prerequisites of the given activity.
     *
     * @param activity the activity for which prerequisites should be retrieved.
     * @return the collection of prerequisites, or an empty collection if the activity has no prerequisites.
     */
    Collection<A> getPrerequisites(A activity);

    /**
     * Returns the start time of an activity. Units for the start time are the same as the activity duration units.
     *
     * @param activity the activity for which to obtain the start time.
     * @return the start time of the activity.
     */
    int getStartTime(A activity);

    /**
     * Returns the end time of an activity. Units for the start time are the same as the activity duration units.
     *
     * @param activity the activity for which to obtain the end time.
     * @return the end time of the activity.
     */
    int getEndTime(A activity);

}
