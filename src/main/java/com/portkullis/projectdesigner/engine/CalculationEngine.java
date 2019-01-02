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
     * Returns all the prerequisites of the given activity.
     *
     * @param activity the activity for which prerequisites should be retrieved.
     * @return the collection of prerequisites, or an empty collection if the activity has no prerequisites.
     */
    Collection<A> getPrerequisites(A activity);

    /**
     * Returns all the successors of the given activity.
     *
     * @param activity the activity for which successors should be retrieved.
     * @return the collection of successors, or an empty collection if the activity has no successors.
     */
    Collection<A> getSuccessors(A activity);

    /**
     * Returns the earliest start time of an activity. Units for the start time are the same as the activity duration units.
     *
     * @param activity the activity for which to obtain the start time.
     * @return the start time of the activity.
     */
    int getEarliestStartTime(A activity);

    /**
     * Returns the earliest end time of an activity. Units for the end time are the same as the activity duration units.
     *
     * @param activity the activity for which to obtain the end time.
     * @return the end time of the activity.
     */
    int getEarliestEndTime(A activity);

    /**
     * Returns the latest start time of an activity. Units for the start time are the same as the activity duration units.
     *
     * @param activity the activity for which to obtain the start time.
     * @return the start time of the activity.
     */
    int getLatestStartTime(A activity);

    /**
     * Returns the latest end time of an activity. Units for the end time are the same as the activity duration units.
     *
     * @param activity the activity for which to obtain the end time.
     * @return the end time of the activity.
     */
    int getLatestEndTime(A activity);

    /**
     * Returns the total float of an activity.
     *
     * @param activity the activity for which total float should be calculated.
     * @return the total float of the activity.
     */
    int getTotalFloat(A activity);

}
