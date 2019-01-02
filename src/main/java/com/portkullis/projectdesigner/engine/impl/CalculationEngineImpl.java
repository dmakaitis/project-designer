package com.portkullis.projectdesigner.engine.impl;

import com.portkullis.projectdesigner.engine.CalculationEngine;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.emptySet;

public class CalculationEngineImpl<A> implements CalculationEngine<A> {

    private final Function<A, Collection<A>> directPrerequisiteCalculator;
    private final Function<A, Collection<A>> directSuccessorsCalculator;
    private final Function<A, Integer> activityDurationCalculator;

    private final Map<A, Collection<A>> prerequisiteCache = new HashMap<>();
    private final Map<A, Collection<A>> successorCache = new HashMap<>();
    private final Map<A, Integer> earlyStartCache = new HashMap<>();
    private final Map<A, Integer> earlyEndCache = new HashMap<>();
    private final Map<A, Integer> lateStartCache = new HashMap<>();
    private final Map<A, Integer> lateEndCache = new HashMap<>();

    public CalculationEngineImpl(Function<A, Collection<A>> directPrerequisiteCalculator, Function<A, Collection<A>> directSuccessorsCalculator, Function<A, Integer> activityDurationCalculator) {
        this.directPrerequisiteCalculator = directPrerequisiteCalculator;
        this.directSuccessorsCalculator = directSuccessorsCalculator;
        this.activityDurationCalculator = activityDurationCalculator;
    }

    @Override
    public Collection<A> getPrerequisites(A activity) {
        return getRelatedActivities(activity, prerequisiteCache, directPrerequisiteCalculator);
    }

    @Override
    public Collection<A> getSuccessors(A activity) {
        return getRelatedActivities(activity, successorCache, directSuccessorsCalculator);
    }

    private static <A> Collection<A> getRelatedActivities(A activity, Map<A, Collection<A>> cache, Function<A, Collection<A>> directRelatedActivityCalculator) {
        return cache.computeIfAbsent(activity, current -> {
            HashSet<A> relatives = new HashSet<>();

            Collection<A> directRelatives = directRelatedActivityCalculator.apply(current);
            directRelatives = directRelatives == null ? emptySet() : directRelatives;
            directRelatives.forEach(a -> {
                if (!relatives.contains(a)) {
                    relatives.add(a);
                    relatives.addAll(getRelatedActivities(a, cache, directRelatedActivityCalculator));
                }
            });

            return relatives;
        });
    }

    @Override
    public int getEarliestStartTime(A activity) {
        return earlyStartCache.computeIfAbsent(activity, a -> getPrerequisites(a).stream()
                .map(this::getEarliestEndTime)
                .max(Integer::compareTo)
                .orElse(0));
    }

    @Override
    public int getEarliestEndTime(A activity) {
        return earlyEndCache.computeIfAbsent(activity, a -> getEarliestStartTime(a) + activityDurationCalculator.apply(a));
    }

    @Override
    public int getLatestStartTime(A activity) {
        return lateStartCache.computeIfAbsent(activity, a -> getLatestEndTime(a) - activityDurationCalculator.apply(a));
    }

    @Override
    public int getLatestEndTime(A activity) {
        return lateEndCache.computeIfAbsent(activity, a -> getSuccessors(a).stream()
                .map(this::getLatestStartTime)
                .min(Integer::compareTo)
                .orElse(getEarliestEndTime(a)));
    }

    @Override
    public int getTotalFloat(A activity) {
        return getLatestStartTime(activity) - getEarliestStartTime(activity);
    }

}
