package com.portkullis.projectdesigner.engine.impl


import spock.lang.Specification

class CalculationEngineImplTest extends Specification {

    def static activity1 = "Activity 1"
    def static activity2 = "Activity 2"
    def static activity3 = "Activity 3"
    def static activity4 = "Activity 4"

    def static prerequisites = [
            (activity1): [],
            (activity2): [activity1],
            (activity3): [activity1],
            (activity4): [activity2, activity3]
    ]
    def static directPrerequisiteCalculator = { prerequisites[it] }

    def static durations = [
            (activity1): 10,
            (activity2): 15,
            (activity3): 25,
            (activity4): 20
    ]
    def static activityDurationCalculator = { durations[it] }

    def engine = new CalculationEngineImpl(directPrerequisiteCalculator, activityDurationCalculator)

    def "Prerequisites for an activity with no prerequisites should be empty"() {
        expect:
        engine.getPrerequisites(activity1).asList() == []
    }

    def "Prerequisites for an activity with one prerequisite should include that one prerequisite"() {
        expect:
        engine.getPrerequisites(activity2).asList() == [activity1]
    }

    def "Prerequisites for an activity with indirect prerequisites should include all prerequisites"() {
        expect:
        engine.getPrerequisites(activity4).containsAll([activity1, activity2, activity3])
    }

    def "If the prerequisite calculator returns null, treat the result as having no prerequisites"() {
        expect:
        engine.getPrerequisites("Invalid activity").empty
    }

    def "The start time of an activity with no prerequisites is zero"() {
        expect:
        engine.getStartTime(activity1) == 0
    }

    def "The start time of an activity with only one prerequisite is the duration of the prerequisite"() {
        expect:
        engine.getStartTime(activity2) == 10
        engine.getStartTime(activity3) == 10
    }

    def "The start time of an activity with multiple prerequisites is the maximum end time of any of the prerequisites"() {
        expect:
        engine.getStartTime(activity4) == 35
    }

    def "The end time of an activity with no prerequisites is the duration of the activity"() {
        expect:
        engine.getEndTime(activity1) == 10
    }

    def "The end time of an activity with only one prerequisite is the duration of the activity plus the end time of the prerequisite"() {
        expect:
        engine.getEndTime(activity2) == 25
        engine.getEndTime(activity3) == 35
    }

    def "The end time of an activity with multiple prerequisites is the maximum end time of any of the prerequisites plus the duration of the activity"() {
        expect:
        engine.getEndTime(activity4) == 55
    }

}
