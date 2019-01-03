package com.portkullis.projectdesigner.adapter

import com.portkullis.projectdesigner.model.Project
import spock.lang.Specification

class ProjectDataAdapterTest extends Specification {


    def static activity1 = "Activity 1"
    def static activity2 = "Activity 2"
    def static activity3 = "Activity 3"

    def static dev1 = "Developer 1"
    def static dev2 = "Developer 2"
    def static dev3 = "Developer 3"

    def project = new Project<String, String>()
    def adapter = new ProjectAssignmentDataAdapter(project)

    def "Retrieving the resources for a project should return a collection of those resources"() {
        expect:
        adapter.getResources() == project.getResources()
    }

    def "Retrieving the resources for a project should return an unmodifiable collection"() {
        when:
        adapter.getResources().add("test")

        then:
        thrown Exception
    }

    def "Retrieving the activities for a project should return a collection of those activities"() {
        expect:
        adapter.getActivities() == project.getUtilityData()
    }

    def "Retrieving the activities for a project should return an unmodifiable collection"() {
        when:
        adapter.getActivities().add("test")

        then:
        thrown Exception
    }

    def "Assigning a resource to an activity for a project should put that resource into the activity's assignment map for the project"() {
        when:
        adapter.assignActivityToResource(activity1, dev1)

        then:
        project.activityAssignments[activity1].containsAll([dev1])
    }

    def "Assigning multiple resources to an activity for a project should put all those resources into the activity's assignment map for the project"() {
        when:
        adapter.assignActivityToResource(activity1, dev1)
        adapter.assignActivityToResource(activity1, dev2)

        then:
        project.activityAssignments[activity1].containsAll([dev1, dev2])
    }

}
