package com.portkullis.projectdesigner.engine.impl

import com.portkullis.projectdesigner.engine.CalculationEngine
import spock.lang.Specification

class CalculationEngineImplTest extends Specification {

    def activity1 = Mock(CalculationEngine.ActivityData)
    def activity2 = Mock(CalculationEngine.ActivityData)
    def activity3 = Mock(CalculationEngine.ActivityData)
    def activity4 = Mock(CalculationEngine.ActivityData)

    def engine = new CalculationEngineImpl()

    void setup() {
        activity1.prerequisites >> []
        activity1.successors >> [activity2, activity3]
        activity1.duration >> 10

        activity2.prerequisites >> [activity1]
        activity2.successors >> [activity4]
        activity2.duration >> 15

        activity3.prerequisites >> [activity1]
        activity3.successors >> [activity4]
        activity3.duration >> 25

        activity4.prerequisites >> [activity2, activity3]
        activity4.successors >> []
        activity4.duration >> 20
    }

    def "The earliest start time of an activity with no prerequisites is zero"() {
        expect:
        engine.getEarliestStartTime(activity1) == 0
    }

    def "The earliest start time of an activity with only one prerequisite is the end time of the prerequisite"() {
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
