package com.portkullis.projectdesigner.engine;

import java.util.Collection;

/**
 * Methods that perform calculations on projects.
 *
 * @author darius
 */
public interface CalculationEngine {

    /**
     * Returns the earliest start time of an activity. Units for the start time are the same as the activity duration units.
     *
     * @param activity the activity for which to obtain the start time.
     * @return the start time of the activity.
     */
    int getEarliestStartTime(ActivityData activity);

    /**
     * Returns the earliest end time of an activity. Units for the end time are the same as the activity duration units.
     *
     * @param activity the activity for which to obtain the end time.
     * @return the end time of the activity.
     */
    int getEarliestEndTime(ActivityData activity);

    /**
     * Returns the latest start time of an activity. Units for the start time are the same as the activity duration units.
     *
     * @param activity the activity for which to obtain the start time.
     * @return the start time of the activity.
     */
    int getLatestStartTime(ActivityData activity);

    /**
     * Returns the latest end time of an activity. Units for the end time are the same as the activity duration units.
     *
     * @param activity the activity for which to obtain the end time.
     * @return the end time of the activity.
     */
    int getLatestEndTime(ActivityData activity);

    /**
     * Returns the total float of an activity.
     *
     * @param activity the activity for which total float should be calculated.
     * @return the total float of the activity.
     */
    int getTotalFloat(ActivityData activity);

    /**
     * Activity data interface.
     */
    interface ActivityData {

        /**
         * Returns the duration of the activity.
         *
         * @return the duration of the activity.
         */
        int getDuration();

        /**
         * Returns the direct prerequisites for the activity.
         *
         * @return the direct prerequisites for the activity.
         */
        Collection<ActivityData> getPrerequisites();

        /**
         * Returns the direct successors for the activity.
         *
         * @return the direct successors for the activity.
         */
        Collection<ActivityData> getSuccessors();

    }

}
