package com.portkullis.projectdesigner.engine.impl


import com.portkullis.projectdesigner.exception.ProjectDesignerRuntimeException
import com.portkullis.projectdesigner.model.Project
import spock.lang.Specification

class AssignmentEngineImplTest extends Specification {

    def static engine = new AssignmentEngineImpl<String, String>({ a, b -> a.compareTo(b) })

    def static activity1 = "Activity 1"
    def static activity2 = "Activity 2"
    def static activity3 = "Activity 3"

    def static dev1 = "Developer 1"
    def static dev2 = "Developer 2"
    def static dev3 = "Developer 3"

    def project = new Project<String>()

    void setup() {
        project.utilityData << activity1
        project.utilityData << activity2
        project.utilityData << activity3

        project.resources << dev1
        project.resources << dev2
        project.resources << dev3
    }

    def "Assigning a non-existant resource to a project should fail"() {
        when:
        engine.assignResourceToActivity(project, "Unknown Resource", activity1)

        then:
        thrown ProjectDesignerRuntimeException
    }

    def "Assigning a resource to a non-existant activity should fail"() {
        when:
        engine.assignResourceToActivity(project, "Developer 1", "Unknown Activity")

        then:
        thrown ProjectDesignerRuntimeException
    }

    def "Assigning a resource to an activity for a project should put that resource into the activity's assignment map for the project"() {
        when:
        engine.assignResourceToActivity(project, dev1, activity1)

        then:
        project.activityAssignments[activity1].contains(dev1)
    }

    def "Assigning multiple resources to an activity for a project should put all those resources into the activity's assignment map for the project"() {
        when:
        engine.assignResourceToActivity(project, dev1, activity1)
        engine.assignResourceToActivity(project, dev2, activity1)

        then:
        project.activityAssignments[activity1].contains(dev1)
        project.activityAssignments[activity1].contains(dev2)
    }

    def "Assigning a resource to an activity for a project should put that activity into the resources's assignment map for the project"() {
        when:
        engine.assignResourceToActivity(project, dev1, activity1)

        then:
        project.resourceAssignments[dev1].contains(activity1)
    }

    def "Assigning a resource to multiple activities for a project should put those activities into the resources's assignment map for the project"() {
        when:
        engine.assignResourceToActivity(project, dev1, activity1)
        engine.assignResourceToActivity(project, dev1, activity2)

        then:
        project.resourceAssignments[dev1].containsAll([activity1, activity2])
    }

    def "Activities in a resource assignment map must always be sorted in order"() {
        when:
        engine.assignResourceToActivity(project, dev1, activity3)
        engine.assignResourceToActivity(project, dev1, activity1)
        engine.assignResourceToActivity(project, dev1, activity2)

        then:
        project.resourceAssignments[dev1].asList() == [activity1, activity2, activity3]
    }

}
