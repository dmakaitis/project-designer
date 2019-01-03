package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.engine.CalculationEngine;

public class CalculationEngineImpl<A> implements CalculationEngine<A> {

    @Override
    public int getEarliestStartTime(ActivityData<A> activity) {
        return activity.getPrerequisites().stream()
                .mapToInt(this::getEarliestEndTime)
                .max()
                .orElse(0);
    }

    @Override
    public int getEarliestEndTime(ActivityData<A> activity) {
        return getEarliestStartTime(activity) + activity.getDuration();
    }

    @Override
    public int getLatestStartTime(ActivityData<A> activity) {
        return getLatestEndTime(activity) - activity.getDuration();
    }

    @Override
    public int getLatestEndTime(ActivityData<A> activity) {
        return activity.getSuccessors().stream()
                .mapToInt(this::getLatestStartTime)
                .min()
                .orElse(getEarliestEndTime(activity));
    }

    @Override
    public int getTotalFloat(ActivityData<A> activity) {
        return getLatestStartTime(activity) - getEarliestStartTime(activity);
    }

}
