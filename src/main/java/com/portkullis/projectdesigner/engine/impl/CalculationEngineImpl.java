package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.engine.CalculationEngine;

public class CalculationEngineImpl implements CalculationEngine {

    @Override
    public int getEarliestStartTime(ActivityData activity) {
        return activity.getPrerequisites().stream()
                .mapToInt(this::getEarliestEndTime)
                .max()
                .orElse(0);
    }

    @Override
    public int getEarliestEndTime(ActivityData activity) {
        return getEarliestStartTime(activity) + activity.getDuration();
    }

    @Override
    public int getLatestStartTime(ActivityData activity) {
        return getLatestEndTime(activity) - activity.getDuration();
    }

    @Override
    public int getLatestEndTime(ActivityData activity) {
        return activity.getSuccessors().stream()
                .mapToInt(this::getLatestStartTime)
                .min()
                .orElse(getEarliestEndTime(activity));
    }

    @Override
    public int getTotalFloat(ActivityData activity) {
        return getLatestStartTime(activity) - getEarliestStartTime(activity);
    }

}
