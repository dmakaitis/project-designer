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
    def static directSuccessorCalculator = { a ->
        def rVal = []
        prerequisites.entrySet().forEach({ e ->
            if (e.getValue().contains(a)) {
                rVal.add(e.getKey())
            }
        })
        return rVal
    }

    def static durations = [
            (activity1): 10,
            (activity2): 15,
            (activity3): 25,
            (activity4): 20
    ]
    def static activityDurationCalculator = { durations[it] }

    def engine = new CalculationEngineImpl(directPrerequisiteCalculator, directSuccessorCalculator, activityDurationCalculator)

    def "Prerequisites for an activity with no prerequisites should be empty"() {
        expect:
        engine.getPrerequisites(activity1).empty
    }

    def "Prerequisites for an activity with one prerequisite should include that one prerequisite"() {
        expect:
        engine.getPrerequisites(activity2).asList() == [activity1]
        engine.getPrerequisites(activity3).asList() == [activity1]
    }

    def "Prerequisites for an activity with indirect prerequisites should include all prerequisites"() {
        expect:
        engine.getPrerequisites(activity4).containsAll([activity1, activity2, activity3])
    }

    def "If the prerequisite calculator returns null, treat the result as having no prerequisites"() {
        expect:
        engine.getPrerequisites("Invalid activity").empty
    }

    def "Successors for an activity with no direct successors should be empty"() {
        expect:
        engine.getSuccessors(activity4).empty
    }

    def "Successors for an activity with one successor should include that one successor"() {
        expect:
        engine.getSuccessors(activity2).asList() == [activity4]
        engine.getSuccessors(activity3).asList() == [activity4]
    }

    def "Successors for an activity with indirect successors should include all successors"() {
        expect:
        engine.getSuccessors(activity1).containsAll([activity4, activity2, activity3])
    }

    def "The earliest start time of an activity with no prerequisites is zero"() {
        expect:
        engine.getEarliestStartTime(activity1) == 0
    }

    def "The earliest start time of an activity with only one prerequisite is the duration of the prerequisite"() {
        expect:
        engine.getEarliestStartTime(activity2) == 10
        engine.getEarliestStartTime(activity3) == 10
    }

    def "The earliest start time of an activity with multiple prerequisites is the maximum end time of any of the prerequisites"() {
        expect:
        engine.getEarliestStartTime(activity4) == 35
    }

    def "The earliest end time of an activity with no prerequisites is the duration of the activity"() {
        expect:
        engine.getEarliestEndTime(activity1) == 10
    }

    def "The earliest end time of an activity with only one prerequisite is the duration of the activity plus the end time of the prerequisite"() {
        expect:
        engine.getEarliestEndTime(activity2) == 25
        engine.getEarliestEndTime(activity3) == 35
    }

    def "The earliest end time of an activity with multiple prerequisites is the maximum end time of any of the prerequisites plus the duration of the activity"() {
        expect:
        engine.getEarliestEndTime(activity4) == 55
    }

    def "The latest end time of an activity with no successors is the same as its earliest end time"() {
        expect:
        engine.getLatestEndTime(activity4) == engine.getEarliestEndTime(activity4)
    }

    def "The latest start time of an activity with no successors is the same as its earliest start time"() {
        expect:
        engine.getLatestStartTime(activity4) == engine.getEarliestStartTime(activity4)
    }

    def "The latest end time of an activity with exactly one successor is the same as the latest start time of its successor"() {
        expect:
        engine.getLatestEndTime(activity2) == engine.getLatestStartTime(activity4)
        engine.getLatestEndTime(activity3) == engine.getLatestStartTime(activity4)
    }

    def "The latest start time of an activity with multiple successors is the minimum late start time of the successor activities"() {
        expect:
        engine.getLatestEndTime(activity1) == Math.min(engine.getLatestStartTime(activity2), engine.getLatestStartTime(activity3))
    }

    def "The total float for an activity is equal to the late start time minus the early start time"() {
        expect:
        engine.getTotalFloat(activity1) == 0
        engine.getTotalFloat(activity2) == 10
        engine.getTotalFloat(activity3) == 0
        engine.getTotalFloat(activity4) == 0
    }

}
