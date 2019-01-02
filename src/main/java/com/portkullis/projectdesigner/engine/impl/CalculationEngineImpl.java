package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.engine.CalculationEngine;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;

import static java.util.Collections.emptySet;

public class CalculationEngineImpl<A> implements CalculationEngine<A> {

    private final Function<A, Collection<A>> directPrerequisiteCalculator;
    private final Function<A, Integer> activityDurationCalculator;

    public CalculationEngineImpl(Function<A, Collection<A>> directPrerequisiteCalculator, Function<A, Integer> activityDurationCalculator) {
        this.directPrerequisiteCalculator = directPrerequisiteCalculator;
        this.activityDurationCalculator = activityDurationCalculator;
    }

    @Override
    public Collection<A> getPrerequisites(A activity) {
        HashSet<A> prerequisites = new HashSet<>();

        Collection<A> directPrerequisites = directPrerequisiteCalculator.apply(activity);
        directPrerequisites = directPrerequisites == null ? emptySet() : directPrerequisites;
        directPrerequisites.forEach(a -> {
            prerequisites.add(a);
            prerequisites.addAll(getPrerequisites(a));
        });

        return prerequisites;
    }

    @Override
    public int getStartTime(A activity) {
        return getPrerequisites(activity).stream()
                .map(this::getEndTime)
                .max(Integer::compareTo)
                .orElse(0);
    }

    @Override
    public int getEndTime(A activity) {
        return getStartTime(activity) + activityDurationCalculator.apply(activity);
    }

}
